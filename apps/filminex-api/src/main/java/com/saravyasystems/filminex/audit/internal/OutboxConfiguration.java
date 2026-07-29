package com.saravyasystems.filminex.audit.internal;

import com.saravyasystems.filminex.audit.api.DomainEventHandler;
import com.saravyasystems.filminex.audit.api.DomainEventPublisher;
import java.util.List;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration(proxyBeanMethods = false)
@EnableScheduling
@EnableConfigurationProperties(OutboxProperties.class)
class OutboxConfiguration {

    @Bean
    DomainEventPublisher domainEventPublisher(JdbcClient jdbcClient) {
        return new JdbcDomainEventPublisher(jdbcClient);
    }

    @Bean
    OutboxDispatcher outboxDispatcher(
            JdbcClient jdbcClient,
            List<DomainEventHandler> handlers,
            OutboxProperties properties) {
        return new OutboxDispatcher(jdbcClient, handlers, properties);
    }
}
