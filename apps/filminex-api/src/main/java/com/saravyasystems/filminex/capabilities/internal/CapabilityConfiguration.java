package com.saravyasystems.filminex.capabilities.internal;

import com.saravyasystems.filminex.audit.api.AuditService;
import com.saravyasystems.filminex.capabilities.api.CapabilityService;
import com.saravyasystems.filminex.identity.api.IdentityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration(proxyBeanMethods = false)
class CapabilityConfiguration {

    @Bean
    CapabilityService capabilityService(
            JdbcClient jdbcClient, IdentityService identityService, AuditService auditService) {
        return new JdbcCapabilityService(jdbcClient, identityService, auditService);
    }
}
