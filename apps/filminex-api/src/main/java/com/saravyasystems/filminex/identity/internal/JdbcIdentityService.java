package com.saravyasystems.filminex.identity.internal;

import com.saravyasystems.filminex.audit.api.DomainEvent;
import com.saravyasystems.filminex.audit.api.DomainEventPublisher;
import com.saravyasystems.filminex.identity.api.IdentityService;
import com.saravyasystems.filminex.identity.api.UserIdentity;
import com.saravyasystems.filminex.identity.api.Workspace;
import com.saravyasystems.filminex.identity.api.WorkspaceAccessDeniedException;
import com.saravyasystems.filminex.identity.api.WorkspaceMembership;
import com.saravyasystems.filminex.identity.api.WorkspaceRole;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.annotation.Transactional;

final class JdbcIdentityService implements IdentityService {

    private final JdbcClient jdbcClient;
    private final DomainEventPublisher eventPublisher;

    JdbcIdentityService(JdbcClient jdbcClient, DomainEventPublisher eventPublisher) {
        this.jdbcClient = jdbcClient;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public UserIdentity registerUser(String email, String displayName) {
        UUID id = UUID.randomUUID();
        return jdbcClient.sql("""
                        insert into filminex.filminex_user (id, email, display_name)
                        values (:id, :email, :displayName)
                        returning id, email, display_name, created_at
                        """)
                .param("id", id)
                .param("email", normalizeEmail(email))
                .param("displayName", requireText(displayName, "displayName"))
                .query(JdbcIdentityService::mapUser)
                .single();
    }

    @Override
    @Transactional
    public Workspace createWorkspace(UUID ownerUserId, String name) {
        requireUser(ownerUserId);
        UUID workspaceId = UUID.randomUUID();
        Workspace workspace = jdbcClient.sql("""
                        insert into filminex.workspace (id, name)
                        values (:id, :name)
                        returning id, name, created_at
                        """)
                .param("id", workspaceId)
                .param("name", requireText(name, "name"))
                .query(JdbcIdentityService::mapWorkspace)
                .single();
        jdbcClient.sql("""
                        insert into filminex.workspace_membership (workspace_id, user_id, role)
                        values (:workspaceId, :userId, 'ADMIN')
                        """)
                .param("workspaceId", workspaceId)
                .param("userId", ownerUserId)
                .update();
        publish(workspaceId, "workspace", workspaceId, "identity.workspace-created.v1",
                "{\"ownerUserId\":\"" + ownerUserId + "\"}");
        return workspace;
    }

    @Override
    @Transactional
    public WorkspaceMembership addMember(
            UUID workspaceId, UUID actorUserId, UUID userId, WorkspaceRole role) {
        lockAndRequireAdmin(workspaceId, actorUserId);
        requireUser(userId);
        WorkspaceMembership membership = jdbcClient.sql("""
                        insert into filminex.workspace_membership (workspace_id, user_id, role)
                        values (:workspaceId, :userId, :role)
                        returning workspace_id, user_id, role, joined_at
                        """)
                .param("workspaceId", workspaceId)
                .param("userId", userId)
                .param("role", role.name())
                .query(JdbcIdentityService::mapMembership)
                .single();
        publishMembership(membership, "identity.member-added.v1");
        return membership;
    }

    @Override
    @Transactional
    public WorkspaceMembership changeRole(
            UUID workspaceId, UUID actorUserId, UUID userId, WorkspaceRole role) {
        lockAndRequireAdmin(workspaceId, actorUserId);
        WorkspaceMembership current = findMembership(workspaceId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User is not a workspace member"));
        if (current.role() == WorkspaceRole.ADMIN && role != WorkspaceRole.ADMIN) {
            requireAnotherAdmin(workspaceId);
        }
        WorkspaceMembership membership = jdbcClient.sql("""
                        update filminex.workspace_membership
                        set role = :role
                        where workspace_id = :workspaceId and user_id = :userId
                        returning workspace_id, user_id, role, joined_at
                        """)
                .param("workspaceId", workspaceId)
                .param("userId", userId)
                .param("role", role.name())
                .query(JdbcIdentityService::mapMembership)
                .single();
        publishMembership(membership, "identity.member-role-changed.v1");
        return membership;
    }

    @Override
    @Transactional
    public void removeMember(UUID workspaceId, UUID actorUserId, UUID userId) {
        lockAndRequireAdmin(workspaceId, actorUserId);
        WorkspaceMembership current = findMembership(workspaceId, userId)
                .orElseThrow(() -> new IllegalArgumentException("User is not a workspace member"));
        if (current.role() == WorkspaceRole.ADMIN) {
            requireAnotherAdmin(workspaceId);
        }
        jdbcClient.sql("""
                        delete from filminex.workspace_membership
                        where workspace_id = :workspaceId and user_id = :userId
                        """)
                .param("workspaceId", workspaceId)
                .param("userId", userId)
                .update();
        publishMembership(current, "identity.member-removed.v1");
    }

    @Override
    public Optional<WorkspaceMembership> findMembership(UUID workspaceId, UUID userId) {
        return jdbcClient.sql("""
                        select workspace_id, user_id, role, joined_at
                        from filminex.workspace_membership
                        where workspace_id = :workspaceId and user_id = :userId
                        """)
                .param("workspaceId", workspaceId)
                .param("userId", userId)
                .query(JdbcIdentityService::mapMembership)
                .optional();
    }

    @Override
    public List<WorkspaceMembership> listMemberships(UUID workspaceId, UUID actorUserId) {
        requireMembership(workspaceId, actorUserId);
        return jdbcClient.sql("""
                        select workspace_id, user_id, role, joined_at
                        from filminex.workspace_membership
                        where workspace_id = :workspaceId
                        order by joined_at, user_id
                        """)
                .param("workspaceId", workspaceId)
                .query(JdbcIdentityService::mapMembership)
                .list();
    }

    private void lockAndRequireAdmin(UUID workspaceId, UUID actorUserId) {
        Integer locked = jdbcClient.sql("select 1 from filminex.workspace where id = :id for update")
                .param("id", workspaceId)
                .query(Integer.class)
                .optional()
                .orElseThrow(() -> new IllegalArgumentException("Workspace does not exist"));
        if (locked != 1 || findMembership(workspaceId, actorUserId)
                .map(WorkspaceMembership::role)
                .filter(WorkspaceRole.ADMIN::equals)
                .isEmpty()) {
            throw new WorkspaceAccessDeniedException("Workspace administration requires ADMIN");
        }
    }

    private void requireMembership(UUID workspaceId, UUID userId) {
        if (findMembership(workspaceId, userId).isEmpty()) {
            throw new WorkspaceAccessDeniedException("Workspace membership is required");
        }
    }

    private void requireAnotherAdmin(UUID workspaceId) {
        Long admins = jdbcClient.sql("""
                        select count(*) from filminex.workspace_membership
                        where workspace_id = :workspaceId and role = 'ADMIN'
                        """)
                .param("workspaceId", workspaceId)
                .query(Long.class)
                .single();
        if (admins <= 1) {
            throw new IllegalStateException("A workspace must retain at least one ADMIN");
        }
    }

    private void requireUser(UUID userId) {
        Boolean exists = jdbcClient.sql("""
                        select exists(
                            select 1 from filminex.filminex_user where id = :userId
                        )
                        """)
                .param("userId", userId)
                .query(Boolean.class)
                .single();
        if (!exists) {
            throw new IllegalArgumentException("User does not exist");
        }
    }

    private void publishMembership(WorkspaceMembership membership, String eventType) {
        publish(
                membership.workspaceId(),
                "workspace-membership",
                membership.userId(),
                eventType,
                "{\"userId\":\"" + membership.userId() + "\",\"role\":\""
                        + membership.role() + "\"}");
    }

    private void publish(
            UUID workspaceId, String aggregateType, UUID aggregateId, String eventType, String payload) {
        eventPublisher.publish(new DomainEvent(
                UUID.randomUUID(),
                workspaceId,
                aggregateType,
                aggregateId.toString(),
                eventType,
                payload,
                Instant.now()));
    }

    private static UserIdentity mapUser(ResultSet resultSet, int rowNumber) throws SQLException {
        return new UserIdentity(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("email"),
                resultSet.getString("display_name"),
                resultSet.getTimestamp("created_at").toInstant());
    }

    private static Workspace mapWorkspace(ResultSet resultSet, int rowNumber) throws SQLException {
        return new Workspace(
                resultSet.getObject("id", UUID.class),
                resultSet.getString("name"),
                resultSet.getTimestamp("created_at").toInstant());
    }

    private static WorkspaceMembership mapMembership(ResultSet resultSet, int rowNumber)
            throws SQLException {
        return new WorkspaceMembership(
                resultSet.getObject("workspace_id", UUID.class),
                resultSet.getObject("user_id", UUID.class),
                WorkspaceRole.valueOf(resultSet.getString("role")),
                resultSet.getTimestamp("joined_at").toInstant());
    }

    private static String normalizeEmail(String email) {
        String normalized = requireText(email, "email").toLowerCase(Locale.ROOT);
        if (!normalized.contains("@")) {
            throw new IllegalArgumentException("email must be valid");
        }
        return normalized;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }
}
