package com.saravyasystems.filminex.audit.api;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/** Public boundary for durable business audit events and provenance history. */
public interface AuditService {
    AuditRecord append(AuditEvent event);
    Optional<AuditRecord> find(UUID workspaceId, UUID eventId);
    List<AuditRecord> query(AuditQuery query);
}
