package com.saravyasystems.filminex.rights.api;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record RightsRequest(
        UUID id,
        UUID workspaceId,
        UUID requestedBy,
        UUID talentId,
        RightsUse use,
        RightsProductionMode productionMode,
        String territory,
        Instant intendedAt,
        UUID correlationId) {

    public RightsRequest {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(workspaceId, "workspaceId");
        Objects.requireNonNull(requestedBy, "requestedBy");
        Objects.requireNonNull(talentId, "talentId");
        Objects.requireNonNull(use, "use");
        Objects.requireNonNull(productionMode, "productionMode");
        territory = requireText(territory, "territory");
        Objects.requireNonNull(intendedAt, "intendedAt");
        correlationId = correlationId == null ? id : correlationId;
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
