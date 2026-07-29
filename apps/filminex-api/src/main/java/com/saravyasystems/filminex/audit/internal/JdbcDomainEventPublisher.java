package com.saravyasystems.filminex.audit.internal;

import com.saravyasystems.filminex.audit.api.DomainEvent;
import com.saravyasystems.filminex.audit.api.DomainEventPublisher;
import java.time.ZoneOffset;
import org.springframework.jdbc.core.simple.JdbcClient;

final class JdbcDomainEventPublisher implements DomainEventPublisher {

    private final JdbcClient jdbcClient;

    JdbcDomainEventPublisher(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public void publish(DomainEvent event) {
        jdbcClient.sql("""
                        insert into filminex.event_outbox (
                            id, workspace_id, aggregate_type, aggregate_id,
                            event_type, payload, occurred_at
                        ) values (
                            :id, :workspaceId, :aggregateType, :aggregateId,
                            :eventType, cast(:payload as jsonb), :occurredAt
                        )
                        """)
                .param("id", event.id())
                .param("workspaceId", event.workspaceId())
                .param("aggregateType", event.aggregateType())
                .param("aggregateId", event.aggregateId())
                .param("eventType", event.eventType())
                .param("payload", event.payload())
                .param("occurredAt", event.occurredAt().atOffset(ZoneOffset.UTC))
                .update();
    }
}
