package com.saravyasystems.filminex.identity.api;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Provider-neutral Filminex user identity. */
public record UserIdentity(UUID id, String email, String displayName, Instant createdAt) {

    public UserIdentity {
        Objects.requireNonNull(id, "id");
        email = requireText(email, "email");
        displayName = requireText(displayName, "displayName");
        Objects.requireNonNull(createdAt, "createdAt");
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
