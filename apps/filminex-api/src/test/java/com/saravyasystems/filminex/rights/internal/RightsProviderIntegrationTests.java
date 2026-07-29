package com.saravyasystems.filminex.rights.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.saravyasystems.filminex.identity.api.IdentityService;
import com.saravyasystems.filminex.identity.api.UserIdentity;
import com.saravyasystems.filminex.identity.api.Workspace;
import com.saravyasystems.filminex.identity.api.WorkspaceAccessDeniedException;
import com.saravyasystems.filminex.identity.api.WorkspaceRole;
import com.saravyasystems.filminex.rights.api.LocalRightsGrant;
import com.saravyasystems.filminex.rights.api.RightsDecisionReason;
import com.saravyasystems.filminex.rights.api.RightsDecisionStatus;
import com.saravyasystems.filminex.rights.api.RightsGrant;
import com.saravyasystems.filminex.rights.api.RightsProductionMode;
import com.saravyasystems.filminex.rights.api.RightsRequest;
import com.saravyasystems.filminex.rights.api.RightsUse;
import com.saravyasystems.filminex.rights.api.TalentRightsProvider;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest(properties = {
    "spring.task.scheduling.enabled=false",
    "filminex.events.outbox.poll-interval=1h"
})
class RightsProviderIntegrationTests {

    private static final Instant NOW = Instant.parse("2026-07-30T00:00:00Z");

    @Autowired
    private TalentRightsProvider rights;

    @Autowired
    private IdentityService identities;

    @Autowired
    private JdbcClient jdbc;

    @BeforeEach
    void prepareDatabase() {
        jdbc.sql("delete from filminex.local_rights_grant").update();
        jdbc.sql("truncate table filminex.audit_event").update();
        jdbc.sql("delete from filminex.workspace_entitlement").update();
        jdbc.sql("delete from filminex.event_outbox").update();
        jdbc.sql("delete from filminex.project").update();
        jdbc.sql("delete from filminex.workspace_membership").update();
        jdbc.sql("delete from filminex.workspace").update();
        jdbc.sql("delete from filminex.filminex_user").update();
    }

    @Test
    void activeGrantAllowsOnlyMatchingUseTerritoryAndTime() {
        Context context = context("active");
        RightsGrant grant = rights.grant(grant(context, RightsUse.LIKENESS, Set.of("IN")));

        assertThat(rights.evaluate(request(context, RightsUse.LIKENESS, "in", NOW)))
                .satisfies(decision -> {
                    assertThat(decision.status()).isEqualTo(RightsDecisionStatus.ALLOWED);
                    assertThat(decision.reason())
                            .isEqualTo(RightsDecisionReason.MATCHING_ACTIVE_GRANT);
                    assertThat(decision.evidenceReferences())
                            .containsExactly(grant.evidenceReference());
                });
        assertThat(rights.evaluate(request(context, RightsUse.VOICE, "IN", NOW)).status())
                .isEqualTo(RightsDecisionStatus.REVIEW_REQUIRED);
    }

    @Test
    void revokedAndExpiredGrantsDenyWithExplainableReasons() {
        Context context = context("lifecycle");
        RightsGrant grant = rights.grant(grant(context, RightsUse.VOICE, Set.of("*")));
        rights.revoke(context.workspace().id(), context.owner().id(), grant.id());

        assertThat(rights.evaluate(request(context, RightsUse.VOICE, "US", NOW)).reason())
                .isEqualTo(RightsDecisionReason.GRANT_REVOKED);

        rights.grant(new LocalRightsGrant(
                context.workspace().id(),
                context.owner().id(),
                context.talentId(),
                Set.of(RightsUse.PERFORMANCE),
                Set.of("US"),
                NOW.minus(10, ChronoUnit.DAYS),
                NOW.minus(1, ChronoUnit.DAYS),
                "consent://expired"));
        assertThat(rights.evaluate(request(context, RightsUse.PERFORMANCE, "US", NOW)).reason())
                .isEqualTo(RightsDecisionReason.GRANT_EXPIRED);
    }

    @Test
    void administrationRequiresAdminAndActionsAreAudited() {
        Context context = context("admin");
        UserIdentity editor = user("editor");
        identities.addMember(
                context.workspace().id(), context.owner().id(), editor.id(), WorkspaceRole.EDITOR);

        LocalRightsGrant attempted = new LocalRightsGrant(
                context.workspace().id(),
                editor.id(),
                context.talentId(),
                Set.of(RightsUse.LIKENESS),
                Set.of("IN"),
                NOW.minus(1, ChronoUnit.DAYS),
                NOW.plus(1, ChronoUnit.DAYS),
                "consent://editor");
        assertThatThrownBy(() -> rights.grant(attempted))
                .isInstanceOf(WorkspaceAccessDeniedException.class);

        rights.grant(grant(context, RightsUse.LIKENESS, Set.of("IN")));
        rights.evaluate(request(context, RightsUse.LIKENESS, "IN", NOW));
        assertThat(jdbc.sql("""
                                select count(*) from filminex.audit_event
                                where workspace_id = :workspaceId
                                  and action in ('rights.grant-created', 'rights.evaluated')
                                """)
                        .param("workspaceId", context.workspace().id())
                        .query(Long.class)
                        .single())
                .isEqualTo(2);
    }

    @Test
    void grantsAndDecisionsRemainWorkspaceIsolated() {
        Context first = context("first");
        Context second = context("second");
        rights.grant(grant(first, RightsUse.LIKENESS, Set.of("IN")));

        assertThat(rights.evaluate(request(first, RightsUse.LIKENESS, "IN", NOW)).status())
                .isEqualTo(RightsDecisionStatus.ALLOWED);
        assertThat(rights.evaluate(request(second, RightsUse.LIKENESS, "IN", NOW)).status())
                .isEqualTo(RightsDecisionStatus.REVIEW_REQUIRED);
        assertThatThrownBy(() -> rights.list(
                        first.workspace().id(), second.owner().id(), first.talentId()))
                .isInstanceOf(WorkspaceAccessDeniedException.class);
    }

    private LocalRightsGrant grant(Context context, RightsUse use, Set<String> territories) {
        return new LocalRightsGrant(
                context.workspace().id(),
                context.owner().id(),
                context.talentId(),
                Set.of(use),
                territories,
                NOW.minus(1, ChronoUnit.DAYS),
                NOW.plus(1, ChronoUnit.DAYS),
                "consent://" + UUID.randomUUID());
    }

    private RightsRequest request(
            Context context, RightsUse use, String territory, Instant intendedAt) {
        UUID id = UUID.randomUUID();
        return new RightsRequest(
                id,
                context.workspace().id(),
                context.owner().id(),
                context.talentId(),
                use,
                RightsProductionMode.HYBRID,
                territory,
                intendedAt,
                id);
    }

    private Context context(String name) {
        UserIdentity owner = user(name + "-owner");
        return new Context(
                owner,
                identities.createWorkspace(owner.id(), name + " workspace"),
                UUID.randomUUID());
    }

    private UserIdentity user(String name) {
        return identities.registerUser(
                name + "-" + UUID.randomUUID() + "@filminex.test", name);
    }

    private record Context(UserIdentity owner, Workspace workspace, UUID talentId) {}
}
