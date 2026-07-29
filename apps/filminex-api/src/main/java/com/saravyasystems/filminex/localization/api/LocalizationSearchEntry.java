package com.saravyasystems.filminex.localization.api;

import java.util.UUID;

/** Provider-neutral text projection consumed by the search module. */
public record LocalizationSearchEntry(
        UUID trackId,
        UUID cueId,
        UUID sourceId,
        LocalizationKind kind,
        LocaleTag locale,
        String title,
        String text,
        String dialogueReference) {}
