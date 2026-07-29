package com.saravyasystems.filminex.localization.api;

import java.util.Objects;
import java.util.UUID;

public record AddLocalizationCue(
        UUID id,
        UUID workspaceId,
        UUID trackId,
        UUID actorUserId,
        int sequenceNumber,
        long startMilliseconds,
        long endMilliseconds,
        String text,
        String dialogueReference,
        UUID voiceProfileId) {

    public AddLocalizationCue {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(workspaceId, "workspaceId");
        Objects.requireNonNull(trackId, "trackId");
        Objects.requireNonNull(actorUserId, "actorUserId");
        if (sequenceNumber < 1) {
            throw new IllegalArgumentException("sequenceNumber must be positive");
        }
        if (startMilliseconds < 0 || endMilliseconds <= startMilliseconds) {
            throw new IllegalArgumentException("cue timing must be positive and ordered");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }
    }
}
