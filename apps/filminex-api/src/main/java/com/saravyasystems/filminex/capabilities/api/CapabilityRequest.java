package com.saravyasystems.filminex.capabilities.api;

import java.util.Objects;
import java.util.UUID;

/** Context needed to make one capability decision. */
public record CapabilityRequest(
        UUID workspaceId,
        UUID userId,
        Capability capability,
        ProductionMode productionMode,
        boolean providerAvailable) {

    public CapabilityRequest {
        Objects.requireNonNull(workspaceId, "workspaceId");
        Objects.requireNonNull(userId, "userId");
        Objects.requireNonNull(capability, "capability");
        Objects.requireNonNull(productionMode, "productionMode");
    }
}
