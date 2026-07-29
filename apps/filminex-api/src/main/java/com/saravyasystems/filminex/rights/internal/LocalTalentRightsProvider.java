package com.saravyasystems.filminex.rights.internal;

import com.saravyasystems.filminex.audit.api.AuditActorType;
import com.saravyasystems.filminex.audit.api.AuditEvent;
import com.saravyasystems.filminex.audit.api.AuditOutcome;
import com.saravyasystems.filminex.audit.api.AuditService;
import com.saravyasystems.filminex.identity.api.IdentityService;
import com.saravyasystems.filminex.identity.api.WorkspaceAccessDeniedException;
import com.saravyasystems.filminex.identity.api.WorkspaceMembership;
import com.saravyasystems.filminex.identity.api.WorkspaceRole;
import com.saravyasystems.filminex.rights.api.LocalRightsGrant;
import com.saravyasystems.filminex.rights.api.RightsDecision;
import com.saravyasystems.filminex.rights.api.RightsDecisionReason;
import com.saravyasystems.filminex.rights.api.RightsDecisionStatus;
import com.saravyasystems.filminex.rights.api.RightsGrant;
import com.saravyasystems.filminex.rights.api.RightsRequest;
import com.saravyasystems.filminex.rights.api.RightsUse;
import com.saravyasystems.filminex.rights.api.TalentRightsProvider;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

class LocalTalentRightsProvider implements TalentRightsProvider {

    private final JdbcClient jdbc;
    private final IdentityService identities;
    private final AuditService audit;
    private final Clock clock;

    LocalTalentRightsProvider(
            JdbcClient jdbc, IdentityService identities, AuditService audit, Clock clock) {
        this.jdbc = jdbc;
        this.identities = identities;
        this.audit = audit;
        this.clock = clock;
    }

    @Override
    public RightsDecision evaluate(RightsRequest request) {
        requireMember(request.workspaceId(), request.requestedBy());
        Optional<RightsGrant> active = jdbc.sql("""
                        select *
                        from filminex.local_rights_grant
                        where workspace_id = :workspaceId
                          and talent_id = :talentId
                          and revoked = false
                          and :use = any(uses)
                          and (:territory = any(territories) or '*' = any(territories))
                          and valid_from <= :intendedAt
                          and valid_until >= :intendedAt
                        order by changed_at desc
                        limit 1
                        """)
                .param("workspaceId", request.workspaceId())
                .param("talentId", request.talentId())
                .param("use", request.use().name())
                .param("territory", request.territory().toUpperCase())
                .param("intendedAt", request.intendedAt())
                .query(LocalTalentRightsProvider::mapGrant)
                .optional();

        RightsDecision decision = active
                .map(grant -> new RightsDecision(
                        RightsDecisionStatus.ALLOWED,
                        RightsDecisionReason.MATCHING_ACTIVE_GRANT,
                        "local",
                        List.of(grant.evidenceReference())))
                .orElseGet(() -> missingDecision(request));
        appendDecisionAudit(request, decision);
        return decision;
    }

    @Override
    @Transactional
    public RightsGrant grant(LocalRightsGrant request) {
        requireAdmin(request.workspaceId(), request.actorUserId());
        UUID id = UUID.randomUUID();
        RightsGrant result = jdbc.sql("""
                        insert into filminex.local_rights_grant (
                            id, workspace_id, talent_id, uses, territories, valid_from,
                            valid_until, evidence_reference, changed_by
                        ) values (
                            :id, :workspaceId, :talentId,
                            string_to_array(:uses, ','),
                            string_to_array(:territories, ','),
                            :validFrom, :validUntil, :evidence, :changedBy
                        )
                        returning *
                        """)
                .param("id", id)
                .param("workspaceId", request.workspaceId())
                .param("talentId", request.talentId())
                .param("uses", joinUses(request.uses()))
                .param("territories", joinTerritories(request.territories()))
                .param("validFrom", request.validFrom())
                .param("validUntil", request.validUntil())
                .param("evidence", request.evidenceReference())
                .param("changedBy", request.actorUserId())
                .query(LocalTalentRightsProvider::mapGrant)
                .single();
        appendChangeAudit(result, "rights.grant-created");
        return result;
    }

    @Override
    @Transactional
    public void revoke(UUID workspaceId, UUID actorUserId, UUID grantId) {
        requireAdmin(workspaceId, actorUserId);
        RightsGrant grant = jdbc.sql("""
                        update filminex.local_rights_grant
                        set revoked = true, changed_by = :changedBy, changed_at = current_timestamp
                        where id = :id and workspace_id = :workspaceId and revoked = false
                        returning *
                        """)
                .param("changedBy", actorUserId)
                .param("id", grantId)
                .param("workspaceId", workspaceId)
                .query(LocalTalentRightsProvider::mapGrant)
                .optional()
                .orElseThrow(() -> new IllegalArgumentException("Active rights grant not found"));
        appendChangeAudit(grant, "rights.grant-revoked");
    }

    @Override
    public List<RightsGrant> list(UUID workspaceId, UUID actorUserId, UUID talentId) {
        requireMember(workspaceId, actorUserId);
        return jdbc.sql("""
                        select *
                        from filminex.local_rights_grant
                        where workspace_id = :workspaceId and talent_id = :talentId
                        order by changed_at desc
                        """)
                .param("workspaceId", workspaceId)
                .param("talentId", talentId)
                .query(LocalTalentRightsProvider::mapGrant)
                .list();
    }

    private RightsDecision missingDecision(RightsRequest request) {
        Optional<RightsGrant> latest = jdbc.sql("""
                        select *
                        from filminex.local_rights_grant
                        where workspace_id = :workspaceId and talent_id = :talentId
                          and :use = any(uses)
                        order by changed_at desc
                        limit 1
                        """)
                .param("workspaceId", request.workspaceId())
                .param("talentId", request.talentId())
                .param("use", request.use().name())
                .query(LocalTalentRightsProvider::mapGrant)
                .optional();
        if (latest.map(RightsGrant::revoked).orElse(false)) {
            return denied(RightsDecisionReason.GRANT_REVOKED);
        }
        if (latest.filter(grant -> grant.validUntil().isBefore(request.intendedAt())).isPresent()) {
            return denied(RightsDecisionReason.GRANT_EXPIRED);
        }
        return new RightsDecision(
                RightsDecisionStatus.REVIEW_REQUIRED,
                RightsDecisionReason.NO_MATCHING_GRANT,
                "local",
                List.of());
    }

    private static RightsDecision denied(RightsDecisionReason reason) {
        return new RightsDecision(RightsDecisionStatus.DENIED, reason, "local", List.of());
    }

    private void requireAdmin(UUID workspaceId, UUID userId) {
        if (identities.findMembership(workspaceId, userId)
                .map(WorkspaceMembership::role)
                .filter(WorkspaceRole.ADMIN::equals)
                .isEmpty()) {
            throw new WorkspaceAccessDeniedException("Rights administration requires ADMIN");
        }
    }

    private void requireMember(UUID workspaceId, UUID userId) {
        if (identities.findMembership(workspaceId, userId).isEmpty()) {
            throw new WorkspaceAccessDeniedException("Workspace membership is required");
        }
    }

    private void appendDecisionAudit(RightsRequest request, RightsDecision decision) {
        audit.append(new AuditEvent(
                request.workspaceId(),
                AuditActorType.USER,
                request.requestedBy().toString(),
                "rights.evaluated",
                "talent",
                request.talentId().toString(),
                decision.status() == RightsDecisionStatus.ALLOWED
                        ? AuditOutcome.SUCCEEDED
                        : AuditOutcome.DENIED,
                Instant.now(clock),
                request.correlationId(),
                request.id(),
                Map.of(
                        "provider", decision.provider(),
                        "use", request.use().name(),
                        "decision", decision.status().name(),
                        "reason", decision.reason().name())));
    }

    private void appendChangeAudit(RightsGrant grant, String action) {
        audit.append(new AuditEvent(
                grant.workspaceId(),
                AuditActorType.USER,
                grant.changedBy().toString(),
                action,
                "rights-grant",
                grant.id().toString(),
                AuditOutcome.SUCCEEDED,
                Instant.now(clock),
                UUID.randomUUID(),
                null,
                Map.of("talentId", grant.talentId().toString(), "provider", "local")));
    }

    private static String joinUses(Set<RightsUse> uses) {
        if (uses.isEmpty()) {
            throw new IllegalArgumentException("At least one rights use is required");
        }
        return uses.stream().map(Enum::name).sorted().collect(Collectors.joining(","));
    }

    private static String joinTerritories(Set<String> territories) {
        if (territories.isEmpty()) {
            throw new IllegalArgumentException("At least one territory is required");
        }
        return territories.stream()
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.joining(","));
    }

    private static RightsGrant mapGrant(ResultSet resultSet, int rowNumber) throws SQLException {
        return new RightsGrant(
                resultSet.getObject("id", UUID.class),
                resultSet.getObject("workspace_id", UUID.class),
                resultSet.getObject("talent_id", UUID.class),
                enumSet(resultSet.getArray("uses")),
                stringSet(resultSet.getArray("territories")),
                resultSet.getTimestamp("valid_from").toInstant(),
                resultSet.getTimestamp("valid_until").toInstant(),
                resultSet.getString("evidence_reference"),
                resultSet.getBoolean("revoked"),
                resultSet.getObject("changed_by", UUID.class),
                resultSet.getTimestamp("changed_at").toInstant());
    }

    private static Set<RightsUse> enumSet(Array values) throws SQLException {
        return Arrays.stream((String[]) values.getArray())
                .map(RightsUse::valueOf)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static Set<String> stringSet(Array values) throws SQLException {
        return Set.copyOf(Arrays.asList((String[]) values.getArray()));
    }
}
