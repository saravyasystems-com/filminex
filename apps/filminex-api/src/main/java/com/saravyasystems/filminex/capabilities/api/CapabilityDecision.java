package com.saravyasystems.filminex.capabilities.api;

import java.util.Objects;

/** Explainable result consumed consistently by API and user-interface callers. */
public record CapabilityDecision(boolean allowed, CapabilityDecisionReason reason) {

    public CapabilityDecision {
        Objects.requireNonNull(reason, "reason");
    }

    public static CapabilityDecision allow() {
        return new CapabilityDecision(true, CapabilityDecisionReason.ALLOWED);
    }

    public static CapabilityDecision deny(CapabilityDecisionReason reason) {
        if (reason == CapabilityDecisionReason.ALLOWED) {
            throw new IllegalArgumentException("A denied decision requires a denial reason");
        }
        return new CapabilityDecision(false, reason);
    }
}
