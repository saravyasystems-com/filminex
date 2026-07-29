package com.saravyasystems.filminex.identity.internal;

import com.saravyasystems.filminex.audit.api.DomainEventPublisher;
import com.saravyasystems.filminex.audit.api.DomainEventHandler;
import com.saravyasystems.filminex.identity.api.IdentityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration(proxyBeanMethods = false)
class IdentityConfiguration {

    @Bean
    IdentityService identityService(JdbcClient jdbcClient, DomainEventPublisher eventPublisher) {
        return new JdbcIdentityService(jdbcClient, eventPublisher);
    }

    @Bean
    DomainEventHandler identityDomainEventHandler() {
        return new IdentityDomainEventHandler();
    }
}
