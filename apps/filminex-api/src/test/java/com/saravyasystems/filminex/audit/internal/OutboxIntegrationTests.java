package com.saravyasystems.filminex.audit.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.saravyasystems.filminex.audit.api.DomainEvent;
import com.saravyasystems.filminex.audit.api.DomainEventHandler;
import com.saravyasystems.filminex.audit.api.DomainEventPublisher;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest(properties = {
    "spring.task.scheduling.enabled=false",
    "filminex.events.outbox.poll-interval=1h",
    "filminex.events.outbox.retry-delay=1ms"
})
@Import(OutboxIntegrationTests.TestConfig.class)
class OutboxIntegrationTests {

    private static final UUID WORKSPACE_ID =
            UUID.fromString("ca6ff8ef-b9e0-4278-9699-f1b9a1c51813");

    @Autowired
    private DomainEventPublisher publisher;

    @Autowired
    private OutboxDispatcher dispatcher;

    @Autowired
    private JdbcClient jdbcClient;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TestHandler handler;

    @BeforeEach
    void prepareDatabase() {
        jdbcClient.sql("delete from filminex.event_outbox").update();
        jdbcClient.sql("delete from filminex.project").update();
        jdbcClient.sql("delete from filminex.workspace").update();
        jdbcClient.sql("insert into filminex.workspace (id, name) values (:id, 'Outbox Test')")
                .param("id", WORKSPACE_ID)
                .update();
        handler.reset();
    }

    @Test
    void publishesAndDeliversEventExactlyOnce() {
        DomainEvent event = event("test.delivered.v1");

        publisher.publish(event);
        dispatcher.dispatch();
        dispatcher.dispatch();

        assertThat(handler.deliveries()).isEqualTo(1);
        assertThat(status(event.id())).isEqualTo("PROCESSED");
        assertThat(attempts(event.id())).isEqualTo(1);
    }

    @Test
    void retriesAHandlerFailureWithoutLosingTheEvent() {
        handler.failNextDelivery();
        DomainEvent event = event("test.retried.v1");
        publisher.publish(event);

        dispatcher.dispatch();

        assertThat(status(event.id())).isEqualTo("PENDING");
        assertThat(attempts(event.id())).isEqualTo(1);
        assertThat(lastError(event.id())).contains("temporary failure");

        jdbcClient.sql("""
                        update filminex.event_outbox
                        set next_attempt_at = current_timestamp
                        where id = :id
                        """)
                .param("id", event.id())
                .update();
        dispatcher.dispatch();

        assertThat(status(event.id())).isEqualTo("PROCESSED");
        assertThat(attempts(event.id())).isEqualTo(2);
        assertThat(handler.deliveries()).isEqualTo(1);
    }

    @Test
    void eventRollsBackWithTheAuthoritativeTransaction() {
        DomainEvent event = event("test.rolled-back.v1");
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);

        assertThatThrownBy(() -> transaction.executeWithoutResult(ignored -> {
                    publisher.publish(event);
                    throw new IllegalStateException("rollback");
                }))
                .isInstanceOf(IllegalStateException.class);

        Long count = jdbcClient.sql(
                        "select count(*) from filminex.event_outbox where id = :id")
                .param("id", event.id())
                .query(Long.class)
                .single();
        assertThat(count).isZero();
    }

    private DomainEvent event(String type) {
        return new DomainEvent(
                UUID.randomUUID(),
                WORKSPACE_ID,
                "project",
                UUID.randomUUID().toString(),
                type,
                "{\"name\":\"Foundation\"}",
                Instant.now());
    }

    private String status(UUID id) {
        return jdbcClient.sql("select status from filminex.event_outbox where id = :id")
                .param("id", id)
                .query(String.class)
                .single();
    }

    private Integer attempts(UUID id) {
        return jdbcClient.sql("select attempts from filminex.event_outbox where id = :id")
                .param("id", id)
                .query(Integer.class)
                .single();
    }

    private String lastError(UUID id) {
        return jdbcClient.sql("select last_error from filminex.event_outbox where id = :id")
                .param("id", id)
                .query(String.class)
                .single();
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        TestHandler testHandler() {
            return new TestHandler();
        }
    }

    static final class TestHandler implements DomainEventHandler {

        private final AtomicInteger deliveries = new AtomicInteger();
        private boolean failNext;

        @Override
        public boolean supports(String eventType) {
            return eventType.startsWith("test.");
        }

        @Override
        public void handle(DomainEvent event) {
            if (failNext) {
                failNext = false;
                throw new IllegalStateException("temporary failure");
            }
            deliveries.incrementAndGet();
        }

        void failNextDelivery() {
            failNext = true;
        }

        int deliveries() {
            return deliveries.get();
        }

        void reset() {
            failNext = false;
            deliveries.set(0);
        }
    }
}
