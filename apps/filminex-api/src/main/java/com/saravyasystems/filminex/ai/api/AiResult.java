package com.saravyasystems.filminex.ai.api;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Normalized, reviewable AI output with provider provenance. */
public record AiResult(
        UUID requestId,
        String provider,
        String model,
        String output,
        String finishReason,
        Map<String, String> safetyMetadata,
        AiUsage usage,
        Instant completedAt) {

    public AiResult {
        Objects.requireNonNull(requestId, "requestId");
        provider = requireText(provider, "provider");
        model = requireText(model, "model");
        output = requireText(output, "output");
        finishReason = requireText(finishReason, "finishReason");
        safetyMetadata =
                safetyMetadata == null ? Map.of() : Map.copyOf(safetyMetadata);
        Objects.requireNonNull(usage, "usage");
        Objects.requireNonNull(completedAt, "completedAt");
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
