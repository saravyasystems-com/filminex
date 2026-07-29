package com.saravyasystems.filminex.audit.internal;

import com.saravyasystems.filminex.audit.api.AuditService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;
import tools.jackson.databind.ObjectMapper;

@Configuration(proxyBeanMethods = false)
class AuditConfiguration {

    @Bean
    AuditService auditService(JdbcClient jdbcClient, ObjectMapper objectMapper) {
        return new JdbcAuditService(jdbcClient, objectMapper);
    }
}
