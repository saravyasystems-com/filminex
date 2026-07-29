package com.saravyasystems.filminex.audit.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.saravyasystems.filminex.audit.api.AuditActorType;
import com.saravyasystems.filminex.audit.api.AuditEvent;
import com.saravyasystems.filminex.audit.api.AuditOutcome;
import com.saravyasystems.filminex.audit.api.AuditQuery;
import com.saravyasystems.filminex.audit.api.AuditRecord;
import com.saravyasystems.filminex.audit.api.AuditService;
import com.saravyasystems.filminex.identity.api.IdentityService;
import com.saravyasystems.filminex.identity.api.UserIdentity;
import com.saravyasystems.filminex.identity.api.Workspace;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest(properties = {
    "spring.task.scheduling.enabled=false",
    "filminex.events.outbox.poll-interval=1h"
})
class AuditIntegrationTests {

    @Autowired
    private AuditService audit;

    @Autowired
    private IdentityService identities;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void prepareDatabase() {
        jdbcClient.sql("truncate table filminex.audit_event").update();
        jdbcClient.sql("delete from filminex.event_outbox").update();
        jdbcClient.sql("delete from filminex.project").update();
        jdbcClient.sql("delete from filminex.workspace_membership").update();
        jdbcClient.sql("delete from filminex.workspace").update();
        jdbcClient.sql("delete from filminex.filminex_user").update();
    }

    @AfterEach
    void removeAuditFixtures() {
        jdbcClient.sql("truncate table filminex.audit_event").update();
    }

    @Test
    void appendsAndReadsStructuredAuditEvidence() {
        Context context = context("evidence");
        UUID correlationId = UUID.randomUUID();
        Instant occurredAt = Instant.parse("2026-07-30T10:15:30Z");

        AuditRecord record = audit.append(new AuditEvent(
                context.workspace().id(),
                AuditActorType.USER,
                context.user().id().toString(),
                "identity.member-role.changed",
                "workspace-membership",
                UUID.randomUUID().toString(),
                AuditOutcome.SUCCEEDED,
                occurredAt,
                correlationId,
                null,
                Map.of("fromRole", "VIEWER", "toRole", "EDITOR")));

        assertThat(audit.find(context.workspace().id(), record.id()))
                .get()
                .satisfies(found -> {
                    assertThat(found.correlationId()).isEqualTo(correlationId);
                    assertThat(found.occurredAt()).isEqualTo(occurredAt);
                    assertThat(found.details())
                            .containsEntry("fromRole", "VIEWER")
                            .containsEntry("toRole", "EDITOR");
                });
    }

    @Test
    void queriesRemainWorkspaceScopedAndSupportMeaningfulFilters() {
        Context first = context("first");
        Context second = context("second");
        UUID subjectId = UUID.randomUUID();
        append(first, "asset.created", subjectId, AuditOutcome.SUCCEEDED);
        append(first, "asset.deleted", subjectId, AuditOutcome.DENIED);
        append(second, "asset.created", subjectId, AuditOutcome.SUCCEEDED);

        AuditQuery deniedForSubject = new AuditQuery(
                first.workspace().id(),
                null,
                null,
                null,
                "asset",
                subjectId.toString(),
                AuditOutcome.DENIED,
                null,
                null,
                null,
                20);

        assertThat(audit.query(deniedForSubject))
                .singleElement()
                .extracting(AuditRecord::action)
                .isEqualTo("asset.deleted");
        assertThat(audit.query(AuditQuery.recent(first.workspace().id(), 20))).hasSize(2);
    }

    @Test
    void databaseRejectsUpdatesAndDeletesToPreserveEvidence() {
        Context context = context("immutable");
        AuditRecord record =
                append(context, "project.created", UUID.randomUUID(), AuditOutcome.SUCCEEDED);

        assertThatThrownBy(() -> jdbcClient.sql("""
                                update filminex.audit_event
                                set action = 'project.rewritten'
                                where id = :id
                                """)
                        .param("id", record.id())
                        .update())
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("append-only");
        assertThatThrownBy(() -> jdbcClient.sql("delete from filminex.audit_event where id = :id")
                        .param("id", record.id())
                        .update())
                .isInstanceOf(DataAccessException.class)
                .hasMessageContaining("append-only");
    }

    @Test
    void rejectsSensitiveDetailsBeforeTheyReachDurableHistory() {
        Context context = context("redaction");

        assertThatThrownBy(() -> audit.append(new AuditEvent(
                        context.workspace().id(),
                        AuditActorType.USER,
                        context.user().id().toString(),
                        "ai.requested",
                        "prompt",
                        "prompt-1",
                        AuditOutcome.SUCCEEDED,
                        Instant.now(),
                        UUID.randomUUID(),
                        null,
                        Map.of("authorizationToken", "must-not-be-stored"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sensitive key");
        assertThat(audit.query(AuditQuery.recent(context.workspace().id(), 10))).isEmpty();
    }

    @Test
    void validatesActorIdentityQueryBoundsAndTimeRange() {
        Context context = context("validation");

        assertThatThrownBy(() -> audit.append(new AuditEvent(
                        context.workspace().id(),
                        AuditActorType.USER,
                        null,
                        "project.created",
                        "project",
                        "project-1",
                        AuditOutcome.SUCCEEDED,
                        Instant.now(),
                        null,
                        null,
                        Map.of())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("actorId");
        assertThatThrownBy(() -> audit.query(AuditQuery.recent(context.workspace().id(), 501)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("limit");
        assertThatThrownBy(() -> audit.query(new AuditQuery(
                        context.workspace().id(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        Instant.parse("2026-08-01T00:00:00Z"),
                        Instant.parse("2026-07-01T00:00:00Z"),
                        10)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("occurredFrom");
    }

    private AuditRecord append(
            Context context, String action, UUID subjectId, AuditOutcome outcome) {
        return audit.append(new AuditEvent(
                context.workspace().id(),
                AuditActorType.USER,
                context.user().id().toString(),
                action,
                "asset",
                subjectId.toString(),
                outcome,
                Instant.now(),
                UUID.randomUUID(),
                null,
                Map.of("source", "integration-test")));
    }

    private Context context(String name) {
        UserIdentity user = identities.registerUser(name + "@filminex.test", name);
        Workspace workspace = identities.createWorkspace(user.id(), name + " workspace");
        return new Context(user, workspace);
    }

    private record Context(UserIdentity user, Workspace workspace) {}
}
