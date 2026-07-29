package com.saravyasystems.filminex.audit.api;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Request to append one immutable audit event. */
public record AuditEvent(
        UUID workspaceId,
        AuditActorType actorType,
        String actorId,
        String action,
        String subjectType,
        String subjectId,
        AuditOutcome outcome,
        Instant occurredAt,
        UUID correlationId,
        UUID causationId,
        Map<String, String> details) {

    public AuditEvent {
        details = details == null ? Map.of() : Map.copyOf(details);
    }
}
