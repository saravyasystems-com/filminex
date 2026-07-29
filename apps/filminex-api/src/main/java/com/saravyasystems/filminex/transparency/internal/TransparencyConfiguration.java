package com.saravyasystems.filminex.transparency.internal;

import com.saravyasystems.filminex.audit.api.AuditService;
import com.saravyasystems.filminex.transparency.api.MediaTransparencyService;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class TransparencyConfiguration {

    @Bean
    MediaTransparencyService mediaTransparencyService(AuditService auditService, Clock clock) {
        return new DefaultMediaTransparencyService(auditService, clock);
    }
}
