package com.saravyasystems.filminex.localization.api;

import java.util.IllformedLocaleException;
import java.util.Locale;

public record LocaleTag(String value) {

    public LocaleTag {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("locale must not be blank");
        }
        try {
            Locale locale = new Locale.Builder().setLanguageTag(value).build();
            if (locale.getLanguage().isBlank()) {
                throw new IllegalArgumentException("locale must include a language");
            }
            value = locale.toLanguageTag();
        } catch (IllformedLocaleException exception) {
            throw new IllegalArgumentException("locale must be a valid language tag", exception);
        }
    }
}
