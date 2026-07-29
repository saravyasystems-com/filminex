package com.saravyasystems.filminex.identity.api;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Workspace ownership and collaboration boundary. */
public record Workspace(UUID id, String name, Instant createdAt) {

    public Workspace {
        Objects.requireNonNull(id, "id");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        Objects.requireNonNull(createdAt, "createdAt");
    }
}
