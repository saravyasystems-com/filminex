package com.saravyasystems.filminex.localization.api;

import java.time.Instant;
import java.util.UUID;

public record VoiceProfile(
        UUID id,
        UUID workspaceId,
        UUID talentId,
        String label,
        LocaleTag locale,
        VoiceOrigin origin,
        UUID changedBy,
        Instant changedAt) {}
