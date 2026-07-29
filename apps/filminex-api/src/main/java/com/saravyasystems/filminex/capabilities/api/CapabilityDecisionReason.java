package com.saravyasystems.filminex.capabilities.api;

/** Machine-readable explanation for a capability decision. */
public enum CapabilityDecisionReason {
    ALLOWED,
    NOT_A_WORKSPACE_MEMBER,
    ROLE_INSUFFICIENT,
    ENTITLEMENT_REQUIRED,
    DISABLED_BY_WORKSPACE_POLICY,
    PRODUCTION_MODE_INCOMPATIBLE,
    PROVIDER_UNAVAILABLE
}
