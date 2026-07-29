package com.saravyasystems.filminex.capabilities.api;

import java.util.List;
import java.util.UUID;

/** Public boundary for capability and entitlement evaluation. */
public interface CapabilityService {

    CapabilityDecision evaluate(CapabilityRequest request);

    WorkspaceEntitlement setEntitlement(
            UUID workspaceId,
            UUID actorUserId,
            Capability capability,
            EntitlementState state,
            EntitlementSource source);

    List<WorkspaceEntitlement> listEntitlements(UUID workspaceId, UUID actorUserId);
}
