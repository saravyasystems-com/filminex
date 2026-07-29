package com.saravyasystems.filminex.identity.api;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** A user's role inside one workspace. */
public record WorkspaceMembership(
        UUID workspaceId, UUID userId, WorkspaceRole role, Instant joinedAt) {

    public WorkspaceMembership {
        Objects.requireNonNull(workspaceId, "workspaceId");
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(role, "role");
        Objects.requireNonNull(joinedAt, "joinedAt");
    }
}
