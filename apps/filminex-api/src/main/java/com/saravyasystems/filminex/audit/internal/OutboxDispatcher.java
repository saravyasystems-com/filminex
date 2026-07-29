package com.saravyasystems.filminex.audit.internal;

import com.saravyasystems.filminex.audit.api.DomainEvent;
import com.saravyasystems.filminex.audit.api.DomainEventHandler;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

final class OutboxDispatcher {

    private final JdbcClient jdbcClient;
    private final List<DomainEventHandler> handlers;
    private final OutboxProperties properties;

    OutboxDispatcher(
            JdbcClient jdbcClient,
            List<DomainEventHandler> handlers,
            OutboxProperties properties) {
        this.jdbcClient = jdbcClient;
        this.handlers = List.copyOf(handlers);
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "${filminex.events.outbox.poll-interval:1s}")
    @Transactional
    public void dispatch() {
        claimBatch().forEach(this::deliver);
    }

    private List<DomainEvent> claimBatch() {
        return jdbcClient.sql("""
                        with claimed as (
                            select id
                            from filminex.event_outbox
                            where status = 'PENDING'
                              and next_attempt_at <= current_timestamp
                            order by occurred_at
                            for update skip locked
                            limit :batchSize
                        )
                        update filminex.event_outbox event
                        set status = 'PROCESSING',
                            attempts = attempts + 1
                        from claimed
                        where event.id = claimed.id
                        returning event.*
                        """)
                .param("batchSize", properties.batchSize())
                .query(OutboxDispatcher::mapEvent)
                .list();
    }

    private void deliver(DomainEvent event) {
        try {
            List<DomainEventHandler> matching =
                    handlers.stream().filter(handler -> handler.supports(event.eventType())).toList();
            if (matching.isEmpty()) {
                throw new IllegalStateException("No handler for event type " + event.eventType());
            }
            matching.forEach(handler -> handler.handle(event));
            jdbcClient.sql("""
                            update filminex.event_outbox
                            set status = 'PROCESSED',
                                processed_at = current_timestamp,
                                last_error = null
                            where id = :id
                            """)
                    .param("id", event.id())
                    .update();
        } catch (RuntimeException exception) {
            jdbcClient.sql("""
                            update filminex.event_outbox
                            set status = 'PENDING',
                                next_attempt_at = current_timestamp
                                    + (:retryDelayMs * interval '1 millisecond'),
                                last_error = :lastError
                            where id = :id
                            """)
                    .param("id", event.id())
                    .param("retryDelayMs", properties.retryDelay().toMillis())
                    .param("lastError", abbreviate(exception))
                    .update();
        }
    }

    private static DomainEvent mapEvent(ResultSet resultSet, int rowNumber) throws SQLException {
        return new DomainEvent(
                resultSet.getObject("id", java.util.UUID.class),
                resultSet.getObject("workspace_id", java.util.UUID.class),
                resultSet.getString("aggregate_type"),
                resultSet.getString("aggregate_id"),
                resultSet.getString("event_type"),
                resultSet.getString("payload"),
                resultSet.getObject("occurred_at", java.time.OffsetDateTime.class).toInstant());
    }

    private static String abbreviate(RuntimeException exception) {
        String message = exception.getClass().getSimpleName() + ": " + exception.getMessage();
        return message.substring(0, Math.min(message.length(), 2000));
    }
}
