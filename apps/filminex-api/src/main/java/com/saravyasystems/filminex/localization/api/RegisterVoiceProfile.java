package com.saravyasystems.filminex.localization.api;

import java.util.Objects;
import java.util.UUID;

public record RegisterVoiceProfile(
        UUID id,
        UUID workspaceId,
        UUID talentId,
        String label,
        LocaleTag locale,
        VoiceOrigin origin,
        UUID actorUserId) {

    public RegisterVoiceProfile {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(workspaceId, "workspaceId");
        Objects.requireNonNull(talentId, "talentId");
        Objects.requireNonNull(locale, "locale");
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(actorUserId, "actorUserId");
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("label must not be blank");
        }
    }
}
