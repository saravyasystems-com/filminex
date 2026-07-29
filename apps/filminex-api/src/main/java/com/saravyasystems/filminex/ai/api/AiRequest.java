package com.saravyasystems.filminex.ai.api;

import com.saravyasystems.filminex.capabilities.api.ProductionMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** Provider-neutral AI task submitted by a Filminex module. */
public record AiRequest(
        UUID id,
        UUID workspaceId,
        UUID requestedBy,
        AiTaskType taskType,
        ProductionMode productionMode,
        String instruction,
        String negativePrompt,
        List<String> referenceAssetKeys,
        Map<String, String> options,
        UUID correlationId) {

    public AiRequest {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(workspaceId, "workspaceId");
        Objects.requireNonNull(requestedBy, "requestedBy");
        Objects.requireNonNull(taskType, "taskType");
        Objects.requireNonNull(productionMode, "productionMode");
        instruction = requireText(instruction, "instruction");
        negativePrompt = negativePrompt == null ? "" : negativePrompt;
        referenceAssetKeys =
                referenceAssetKeys == null ? List.of() : List.copyOf(referenceAssetKeys);
        options = options == null ? Map.of() : Map.copyOf(options);
        correlationId = correlationId == null ? id : correlationId;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
