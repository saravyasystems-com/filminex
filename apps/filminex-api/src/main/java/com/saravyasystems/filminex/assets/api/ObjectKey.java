package com.saravyasystems.filminex.assets.api;

import java.util.Objects;
import java.util.regex.Pattern;

/** Stable, vendor-neutral address of an object stored outside PostgreSQL. */
public record ObjectKey(String value) {

    private static final Pattern SAFE_KEY =
            Pattern.compile("[a-zA-Z0-9][a-zA-Z0-9._/-]{0,1023}");

    public ObjectKey {
        Objects.requireNonNull(value, "value");
        if (!SAFE_KEY.matcher(value).matches()
                || value.contains("//")
                || value.contains("/../")
                || value.startsWith("../")
                || value.endsWith("/..")) {
            throw new IllegalArgumentException("Invalid object key");
        }
    }
}
