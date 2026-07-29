package com.saravyasystems.filminex.audit.api;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Persisted immutable audit event. */
public record AuditRecord(
        UUID id,
        UUID workspaceId,
        AuditActorType actorType,
        String actorId,
        String action,
        String subjectType,
        String subjectId,
        AuditOutcome outcome,
        Instant occurredAt,
        Instant recordedAt,
        UUID correlationId,
        UUID causationId,
        Map<String, String> details) {

    public AuditRecord {
        details = Map.copyOf(details);
    }
}
