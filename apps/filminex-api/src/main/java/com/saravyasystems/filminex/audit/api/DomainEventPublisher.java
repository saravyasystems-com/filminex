package com.saravyasystems.filminex.audit.api;

/** Persists events in the transactional outbox. */
public interface DomainEventPublisher {

    void publish(DomainEvent event);
}
