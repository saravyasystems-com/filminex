package com.saravyasystems.filminex.identity.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Provider-neutral boundary for users, workspaces, and workspace membership. */
public interface IdentityService {

    UserIdentity registerUser(String email, String displayName);

    Workspace createWorkspace(UUID ownerUserId, String name);

    WorkspaceMembership addMember(
            UUID workspaceId, UUID actorUserId, UUID userId, WorkspaceRole role);

    WorkspaceMembership changeRole(
            UUID workspaceId, UUID actorUserId, UUID userId, WorkspaceRole role);

    void removeMember(UUID workspaceId, UUID actorUserId, UUID userId);

    Optional<WorkspaceMembership> findMembership(UUID workspaceId, UUID userId);

    List<WorkspaceMembership> listMemberships(UUID workspaceId, UUID actorUserId);
}
