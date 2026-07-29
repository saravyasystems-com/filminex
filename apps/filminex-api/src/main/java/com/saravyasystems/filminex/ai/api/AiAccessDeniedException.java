package com.saravyasystems.filminex.ai.api;

import com.saravyasystems.filminex.capabilities.api.CapabilityDecisionReason;

/** Raised when the Capability Engine denies an AI task. */
public final class AiAccessDeniedException extends RuntimeException {

    private final CapabilityDecisionReason reason;

    public AiAccessDeniedException(CapabilityDecisionReason reason) {
        super("AI request denied: " + reason);
        this.reason = reason;
    }

    public CapabilityDecisionReason reason() {
        return reason;
    }
}
