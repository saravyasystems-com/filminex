package com.saravyasystems.filminex.ai.api;

/** Normalized provider usage; unknown values are represented by zero. */
public record AiUsage(long inputUnits, long outputUnits) {

    public AiUsage {
        if (inputUnits < 0 || outputUnits < 0) {
            throw new IllegalArgumentException("AI usage cannot be negative");
        }
    }
}
