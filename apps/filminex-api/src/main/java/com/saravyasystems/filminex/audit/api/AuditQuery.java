package com.saravyasystems.filminex.audit.api;

import java.time.Instant;
import java.util.UUID;

/** Workspace-scoped filters for reading audit history. Null optional fields mean no filter. */
public record AuditQuery(
        UUID workspaceId,
        AuditActorType actorType,
        String actorId,
        String action,
        String subjectType,
        String subjectId,
        AuditOutcome outcome,
        UUID correlationId,
        Instant occurredFrom,
        Instant occurredUntil,
        int limit) {

    public static AuditQuery recent(UUID workspaceId, int limit) {
        return new AuditQuery(
                workspaceId, null, null, null, null, null, null, null, null, null, limit);
    }
}
