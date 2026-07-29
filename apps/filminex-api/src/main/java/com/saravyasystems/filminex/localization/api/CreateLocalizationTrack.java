package com.saravyasystems.filminex.localization.api;

import java.util.Objects;
import java.util.UUID;

public record CreateLocalizationTrack(
        UUID id,
        UUID workspaceId,
        UUID sourceId,
        LocalizationKind kind,
        LocaleTag locale,
        String title,
        UUID actorUserId) {

    public CreateLocalizationTrack {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(workspaceId, "workspaceId");
        Objects.requireNonNull(sourceId, "sourceId");
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(locale, "locale");
        Objects.requireNonNull(actorUserId, "actorUserId");
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
    }
}
