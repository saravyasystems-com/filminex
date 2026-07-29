package com.saravyasystems.filminex.identity.internal;

import com.saravyasystems.filminex.audit.api.DomainEvent;
import com.saravyasystems.filminex.audit.api.DomainEventHandler;

/**
 * Marks identity lifecycle events as consumed until Workstream 7 adds audit projections.
 *
 * <p>The durable event remains in the outbox as the authoritative delivery record.
 */
final class IdentityDomainEventHandler implements DomainEventHandler {

    @Override
    public boolean supports(String eventType) {
        return eventType.startsWith("identity.");
    }

    @Override
    public void handle(DomainEvent event) {
        // Workstream 7 will project these durable events into the business audit history.
    }
}
