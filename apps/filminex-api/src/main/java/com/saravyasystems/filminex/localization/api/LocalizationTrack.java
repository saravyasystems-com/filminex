package com.saravyasystems.filminex.localization.api;

import java.time.Instant;
import java.util.UUID;

public record LocalizationTrack(
        UUID id,
        UUID workspaceId,
        UUID sourceId,
        LocalizationKind kind,
        LocaleTag locale,
        String title,
        LocalizationStatus status,
        UUID changedBy,
        Instant changedAt) {}
