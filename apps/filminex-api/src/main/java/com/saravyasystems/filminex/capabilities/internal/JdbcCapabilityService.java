package com.saravyasystems.filminex.capabilities.internal;

import com.saravyasystems.filminex.audit.api.AuditActorType;
import com.saravyasystems.filminex.audit.api.AuditEvent;
import com.saravyasystems.filminex.audit.api.AuditOutcome;
import com.saravyasystems.filminex.audit.api.AuditService;
import com.saravyasystems.filminex.capabilities.api.Capability;
import com.saravyasystems.filminex.capabilities.api.CapabilityDecision;
import com.saravyasystems.filminex.capabilities.api.CapabilityDecisionReason;
import com.saravyasystems.filminex.capabilities.api.CapabilityRequest;
import com.saravyasystems.filminex.capabilities.api.CapabilityService;
import com.saravyasystems.filminex.capabilities.api.EntitlementSource;
import com.saravyasystems.filminex.capabilities.api.EntitlementState;
import com.saravyasystems.filminex.capabilities.api.ProductionMode;
import com.saravyasystems.filminex.capabilities.api.WorkspaceEntitlement;
import com.saravyasystems.filminex.identity.api.IdentityService;
import com.saravyasystems.filminex.identity.api.WorkspaceAccessDeniedException;
import com.saravyasystems.filminex.identity.api.WorkspaceMembership;
import com.saravyasystems.filminex.identity.api.WorkspaceRole;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

class JdbcCapabilityService implements CapabilityService {

    private final JdbcClient jdbcClient;
    private final IdentityService identities;
    private final AuditService audit;

    JdbcCapabilityService(JdbcClient jdbcClient, IdentityService identities, AuditService audit) {
        this.jdbcClient = jdbcClient;
        this.identities = identities;
        this.audit = audit;
    }

    @Override
    public CapabilityDecision evaluate(CapabilityRequest request) {
        Optional<WorkspaceMembership> membership =
                identities.findMembership(request.workspaceId(), request.userId());
        if (membership.isEmpty()) {
            return CapabilityDecision.deny(CapabilityDecisionReason.NOT_A_WORKSPACE_MEMBER);
        }
        if (!roleAllows(membership.orElseThrow().role(), request.capability())) {
            return CapabilityDecision.deny(CapabilityDecisionReason.ROLE_INSUFFICIENT);
        }
        if (isCore(request.capability())) {
            return CapabilityDecision.allow();
        }

        Optional<WorkspaceEntitlement> entitlement =
                findEntitlement(request.workspaceId(), request.capability());
        if (entitlement.isEmpty()) {
            return CapabilityDecision.deny(CapabilityDecisionReason.ENTITLEMENT_REQUIRED);
        }
        if (entitlement.orElseThrow().state() == EntitlementState.DISABLED) {
            return CapabilityDecision.deny(CapabilityDecisionReason.DISABLED_BY_WORKSPACE_POLICY);
        }
        if (!modeAllows(request.capability(), request.productionMode())) {
            return CapabilityDecision.deny(CapabilityDecisionReason.PRODUCTION_MODE_INCOMPATIBLE);
        }
        if (request.capability() == Capability.AI_STUDIO && !request.providerAvailable()) {
            return CapabilityDecision.deny(CapabilityDecisionReason.PROVIDER_UNAVAILABLE);
        }
        return CapabilityDecision.allow();
    }

    @Override
    @Transactional
    public WorkspaceEntitlement setEntitlement(
            UUID workspaceId,
            UUID actorUserId,
            Capability capability,
            EntitlementState state,
            EntitlementSource source) {
        requireAdmin(workspaceId, actorUserId);
        if (isCore(capability)) {
            throw new IllegalArgumentException("Core workspace capabilities are role-derived");
        }
        WorkspaceEntitlement entitlement = jdbcClient.sql("""
                        insert into filminex.workspace_entitlement (
                            workspace_id, capability, state, source, changed_by
                        ) values (
                            :workspaceId, :capability, :state, :source, :changedBy
                        )
                        on conflict (workspace_id, capability) do update
                        set state = excluded.state,
                            source = excluded.source,
                            changed_by = excluded.changed_by,
                            changed_at = current_timestamp
                        returning workspace_id, capability, state, source, changed_by, changed_at
                        """)
                .param("workspaceId", workspaceId)
                .param("capability", capability.name())
                .param("state", state.name())
                .param("source", source.name())
                .param("changedBy", actorUserId)
                .query(JdbcCapabilityService::mapEntitlement)
                .single();
        appendAudit(entitlement);
        return entitlement;
    }

    @Override
    public List<WorkspaceEntitlement> listEntitlements(UUID workspaceId, UUID actorUserId) {
        requireMember(workspaceId, actorUserId);
        return jdbcClient.sql("""
                        select workspace_id, capability, state, source, changed_by, changed_at
                        from filminex.workspace_entitlement
                        where workspace_id = :workspaceId
                        order by capability
                        """)
                .param("workspaceId", workspaceId)
                .query(JdbcCapabilityService::mapEntitlement)
                .list();
    }

    private Optional<WorkspaceEntitlement> findEntitlement(
            UUID workspaceId, Capability capability) {
        return jdbcClient.sql("""
                        select workspace_id, capability, state, source, changed_by, changed_at
                        from filminex.workspace_entitlement
                        where workspace_id = :workspaceId and capability = :capability
                        """)
                .param("workspaceId", workspaceId)
                .param("capability", capability.name())
                .query(JdbcCapabilityService::mapEntitlement)
                .optional();
    }

    private void requireAdmin(UUID workspaceId, UUID userId) {
        if (identities.findMembership(workspaceId, userId)
                .map(WorkspaceMembership::role)
                .filter(WorkspaceRole.ADMIN::equals)
                .isEmpty()) {
            throw new WorkspaceAccessDeniedException("Entitlement administration requires ADMIN");
        }
    }

    private void requireMember(UUID workspaceId, UUID userId) {
        if (identities.findMembership(workspaceId, userId).isEmpty()) {
            throw new WorkspaceAccessDeniedException("Workspace membership is required");
        }
    }

    private void appendAudit(WorkspaceEntitlement entitlement) {
        UUID eventId = UUID.randomUUID();
        audit.append(new AuditEvent(
                entitlement.workspaceId(),
                AuditActorType.USER,
                entitlement.changedBy().toString(),
                "capability.entitlement-changed",
                "workspace-entitlement",
                entitlement.capability().name(),
                AuditOutcome.SUCCEEDED,
                Instant.now(),
                eventId,
                null,
                Map.of(
                        "state", entitlement.state().name(),
                        "source", entitlement.source().name())));
    }

    private static boolean isCore(Capability capability) {
        return capability == Capability.WORKSPACE_READ
                || capability == Capability.WORKSPACE_WRITE
                || capability == Capability.WORKSPACE_ADMIN;
    }

    private static boolean roleAllows(WorkspaceRole role, Capability capability) {
        return switch (capability) {
            case WORKSPACE_READ -> true;
            case WORKSPACE_WRITE, AI_STUDIO, ANIMATION_STUDIO -> role != WorkspaceRole.VIEWER;
            case WORKSPACE_ADMIN -> role == WorkspaceRole.ADMIN;
        };
    }

    private static boolean modeAllows(Capability capability, ProductionMode mode) {
        return switch (capability) {
            case AI_STUDIO -> mode == ProductionMode.AI || mode == ProductionMode.HYBRID;
            case ANIMATION_STUDIO ->
                    mode == ProductionMode.ANIMATION || mode == ProductionMode.HYBRID;
            default -> true;
        };
    }

    private static WorkspaceEntitlement mapEntitlement(ResultSet resultSet, int rowNumber)
            throws SQLException {
        return new WorkspaceEntitlement(
                resultSet.getObject("workspace_id", UUID.class),
                Capability.valueOf(resultSet.getString("capability")),
                EntitlementState.valueOf(resultSet.getString("state")),
                EntitlementSource.valueOf(resultSet.getString("source")),
                resultSet.getObject("changed_by", UUID.class),
                resultSet.getTimestamp("changed_at").toInstant());
    }
}
