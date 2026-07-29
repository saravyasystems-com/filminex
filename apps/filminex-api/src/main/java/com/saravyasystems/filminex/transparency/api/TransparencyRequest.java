package com.saravyasystems.filminex.transparency.api;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Workspace-scoped facts used to derive one MTI decision. */
public record TransparencyRequest(
        UUID id,
        UUID workspaceId,
        String actorId,
        String mediaReference,
        MediaKind mediaKind,
        TransparencyProductionMode productionMode,
        boolean realHumanLikeness,
        boolean realHumanVoice,
        boolean syntheticOrMateriallyAltered,
        List<String> evidenceReferences,
        UUID correlationId) {

    public TransparencyRequest {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(workspaceId, "workspaceId");
        if (actorId == null || actorId.isBlank()) {
            throw new IllegalArgumentException("actorId is required");
        }
        if (mediaReference == null || mediaReference.isBlank()) {
            throw new IllegalArgumentException("mediaReference is required");
        }
        Objects.requireNonNull(mediaKind, "mediaKind");
        Objects.requireNonNull(productionMode, "productionMode");
        evidenceReferences =
                evidenceReferences == null ? List.of() : List.copyOf(evidenceReferences);
        Objects.requireNonNull(correlationId, "correlationId");
    }
}
