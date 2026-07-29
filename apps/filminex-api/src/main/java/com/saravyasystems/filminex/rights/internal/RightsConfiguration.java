package com.saravyasystems.filminex.rights.internal;

import com.saravyasystems.filminex.audit.api.AuditService;
import com.saravyasystems.filminex.identity.api.IdentityService;
import com.saravyasystems.filminex.rights.api.TalentRightsProvider;
import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration(proxyBeanMethods = false)
class RightsConfiguration {

    @Bean
    TalentRightsProvider talentRightsProvider(
            JdbcClient jdbcClient, IdentityService identities, AuditService audit) {
        return new LocalTalentRightsProvider(
                jdbcClient, identities, audit, Clock.systemUTC());
    }
}
