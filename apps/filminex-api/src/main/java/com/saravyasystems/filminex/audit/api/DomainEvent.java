package com.saravyasystems.filminex.audit.api;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Durable fact emitted by an authoritative Filminex transaction. */
public record DomainEvent(
        UUID id,
        UUID workspaceId,
        String aggregateType,
        String aggregateId,
        String eventType,
        String payload,
        Instant occurredAt) {

    public DomainEvent {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(workspaceId, "workspaceId");
        aggregateType = requireText(aggregateType, "aggregateType");
        aggregateId = requireText(aggregateId, "aggregateId");
        eventType = requireText(eventType, "eventType");
        payload = requireText(payload, "payload");
        Objects.requireNonNull(occurredAt, "occurredAt");
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
