package com.saravyasystems.filminex.audit.api;

/** Idempotent consumer of a durable domain event. */
public interface DomainEventHandler {

    boolean supports(String eventType);

    void handle(DomainEvent event);
}
