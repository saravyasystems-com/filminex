package com.saravyasystems.filminex.localization.api;

import java.time.Instant;
import java.util.UUID;

public record LocalizationCue(
        UUID id,
        UUID workspaceId,
        UUID trackId,
        int sequenceNumber,
        long startMilliseconds,
        long endMilliseconds,
        String text,
        String dialogueReference,
        UUID voiceProfileId,
        UUID changedBy,
        Instant changedAt) {}
