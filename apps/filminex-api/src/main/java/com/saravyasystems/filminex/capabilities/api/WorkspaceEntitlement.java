package com.saravyasystems.filminex.capabilities.api;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Current provider-neutral entitlement for one workspace capability. */
public record WorkspaceEntitlement(
        UUID workspaceId,
        Capability capability,
        EntitlementState state,
        EntitlementSource source,
        UUID changedBy,
        Instant changedAt) {

    public WorkspaceEntitlement {
        Objects.requireNonNull(workspaceId, "workspaceId");
        Objects.requireNonNull(capability, "capability");
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(changedBy, "changedBy");
        Objects.requireNonNull(changedAt, "changedAt");
    }
}
