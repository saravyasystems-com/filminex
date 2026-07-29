package com.saravyasystems.filminex.capabilities.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.saravyasystems.filminex.capabilities.api.Capability;
import com.saravyasystems.filminex.capabilities.api.CapabilityDecisionReason;
import com.saravyasystems.filminex.capabilities.api.CapabilityRequest;
import com.saravyasystems.filminex.capabilities.api.CapabilityService;
import com.saravyasystems.filminex.capabilities.api.EntitlementSource;
import com.saravyasystems.filminex.capabilities.api.EntitlementState;
import com.saravyasystems.filminex.capabilities.api.ProductionMode;
import com.saravyasystems.filminex.identity.api.IdentityService;
import com.saravyasystems.filminex.identity.api.UserIdentity;
import com.saravyasystems.filminex.identity.api.Workspace;
import com.saravyasystems.filminex.identity.api.WorkspaceAccessDeniedException;
import com.saravyasystems.filminex.identity.api.WorkspaceRole;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest(properties = {
    "spring.task.scheduling.enabled=false",
    "filminex.events.outbox.poll-interval=1h"
})
class CapabilityIntegrationTests {

    @Autowired
    private CapabilityService capabilities;

    @Autowired
    private IdentityService identities;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void prepareDatabase() {
        jdbcClient.sql("truncate table filminex.audit_event").update();
        jdbcClient.sql("delete from filminex.workspace_entitlement").update();
        jdbcClient.sql("delete from filminex.event_outbox").update();
        jdbcClient.sql("delete from filminex.project").update();
        jdbcClient.sql("delete from filminex.workspace_membership").update();
        jdbcClient.sql("delete from filminex.workspace").update();
        jdbcClient.sql("delete from filminex.filminex_user").update();
    }

    @AfterEach
    void removeCapabilityFixtures() {
        jdbcClient.sql("truncate table filminex.audit_event").update();
        jdbcClient.sql("delete from filminex.workspace_entitlement").update();
    }

    @Test
    void coreCapabilitiesFollowWorkspaceRolesWithoutCommercialEntitlements() {
        Context context = context();
        UserIdentity viewer = user("viewer");
        UserIdentity editor = user("editor");
        identities.addMember(context.workspace().id(), context.owner().id(), viewer.id(), WorkspaceRole.VIEWER);
        identities.addMember(context.workspace().id(), context.owner().id(), editor.id(), WorkspaceRole.EDITOR);

        assertThat(decide(context.workspace().id(), viewer.id(), Capability.WORKSPACE_READ).allowed())
                .isTrue();
        assertThat(decide(context.workspace().id(), viewer.id(), Capability.WORKSPACE_WRITE).reason())
                .isEqualTo(CapabilityDecisionReason.ROLE_INSUFFICIENT);
        assertThat(decide(context.workspace().id(), editor.id(), Capability.WORKSPACE_WRITE).allowed())
                .isTrue();
        assertThat(decide(context.workspace().id(), editor.id(), Capability.WORKSPACE_ADMIN).reason())
                .isEqualTo(CapabilityDecisionReason.ROLE_INSUFFICIENT);
        assertThat(decide(context.workspace().id(), context.owner().id(), Capability.WORKSPACE_ADMIN)
                        .allowed())
                .isTrue();
    }

    @Test
    void optionalStudiosRequireAnExplicitEntitlement() {
        Context context = context();

        assertThat(decide(context.workspace().id(), context.owner().id(), Capability.AI_STUDIO).reason())
                .isEqualTo(CapabilityDecisionReason.ENTITLEMENT_REQUIRED);

        capabilities.setEntitlement(
                context.workspace().id(),
                context.owner().id(),
                Capability.AI_STUDIO,
                EntitlementState.ENABLED,
                EntitlementSource.ADD_ON);

        assertThat(decide(context.workspace().id(), context.owner().id(), Capability.AI_STUDIO)
                        .allowed())
                .isTrue();
    }

    @Test
    void productionModeAndProviderAvailabilityRemainPartOfTheDecision() {
        Context context = context();
        capabilities.setEntitlement(
                context.workspace().id(),
                context.owner().id(),
                Capability.AI_STUDIO,
                EntitlementState.ENABLED,
                EntitlementSource.ADD_ON);

        assertThat(capabilities.evaluate(new CapabilityRequest(
                                context.workspace().id(),
                                context.owner().id(),
                                Capability.AI_STUDIO,
                                ProductionMode.REAL,
                                true))
                        .reason())
                .isEqualTo(CapabilityDecisionReason.PRODUCTION_MODE_INCOMPATIBLE);
        assertThat(capabilities.evaluate(new CapabilityRequest(
                                context.workspace().id(),
                                context.owner().id(),
                                Capability.AI_STUDIO,
                                ProductionMode.HYBRID,
                                false))
                        .reason())
                .isEqualTo(CapabilityDecisionReason.PROVIDER_UNAVAILABLE);
    }

    @Test
    void workspacePolicyCanExplicitlyDisableAnOptionalCapability() {
        Context context = context();
        capabilities.setEntitlement(
                context.workspace().id(),
                context.owner().id(),
                Capability.ANIMATION_STUDIO,
                EntitlementState.DISABLED,
                EntitlementSource.WORKSPACE_POLICY);

        assertThat(capabilities.evaluate(new CapabilityRequest(
                                context.workspace().id(),
                                context.owner().id(),
                                Capability.ANIMATION_STUDIO,
                                ProductionMode.ANIMATION,
                                true))
                        .reason())
                .isEqualTo(CapabilityDecisionReason.DISABLED_BY_WORKSPACE_POLICY);
    }

    @Test
    void onlyAdminsManageEntitlementsAndChangesAreAudited() {
        Context context = context();
        UserIdentity editor = user("editor");
        identities.addMember(context.workspace().id(), context.owner().id(), editor.id(), WorkspaceRole.EDITOR);

        assertThatThrownBy(() -> capabilities.setEntitlement(
                        context.workspace().id(),
                        editor.id(),
                        Capability.AI_STUDIO,
                        EntitlementState.ENABLED,
                        EntitlementSource.SUBSCRIPTION))
                .isInstanceOf(WorkspaceAccessDeniedException.class);

        capabilities.setEntitlement(
                context.workspace().id(),
                context.owner().id(),
                Capability.AI_STUDIO,
                EntitlementState.ENABLED,
                EntitlementSource.SUBSCRIPTION);

        assertThat(capabilities.listEntitlements(context.workspace().id(), editor.id()))
                .singleElement()
                .satisfies(entitlement -> {
                    assertThat(entitlement.capability()).isEqualTo(Capability.AI_STUDIO);
                    assertThat(entitlement.source()).isEqualTo(EntitlementSource.SUBSCRIPTION);
                });
        assertThat(jdbcClient.sql("""
                                select count(*) from filminex.audit_event
                                where workspace_id = :workspaceId
                                  and action = 'capability.entitlement-changed'
                                """)
                        .param("workspaceId", context.workspace().id())
                        .query(Long.class)
                        .single())
                .isOne();
    }

    @Test
    void decisionsAndEntitlementsRemainIsolatedByWorkspace() {
        Context first = context("first");
        Context second = context("second");
        capabilities.setEntitlement(
                first.workspace().id(),
                first.owner().id(),
                Capability.AI_STUDIO,
                EntitlementState.ENABLED,
                EntitlementSource.ADD_ON);

        assertThat(decide(first.workspace().id(), first.owner().id(), Capability.AI_STUDIO).allowed())
                .isTrue();
        assertThat(decide(second.workspace().id(), second.owner().id(), Capability.AI_STUDIO).reason())
                .isEqualTo(CapabilityDecisionReason.ENTITLEMENT_REQUIRED);
        assertThat(decide(first.workspace().id(), second.owner().id(), Capability.WORKSPACE_READ)
                        .reason())
                .isEqualTo(CapabilityDecisionReason.NOT_A_WORKSPACE_MEMBER);
    }

    private com.saravyasystems.filminex.capabilities.api.CapabilityDecision decide(
            UUID workspaceId, UUID userId, Capability capability) {
        return capabilities.evaluate(
                new CapabilityRequest(workspaceId, userId, capability, ProductionMode.HYBRID, true));
    }

    private Context context() {
        return context(UUID.randomUUID().toString());
    }

    private Context context(String name) {
        UserIdentity owner = user(name + "-owner");
        return new Context(owner, identities.createWorkspace(owner.id(), name + " workspace"));
    }

    private UserIdentity user(String name) {
        return identities.registerUser(name + "-" + UUID.randomUUID() + "@filminex.test", name);
    }

    private record Context(UserIdentity owner, Workspace workspace) {}
}
