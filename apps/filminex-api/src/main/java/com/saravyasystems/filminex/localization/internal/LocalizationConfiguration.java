package com.saravyasystems.filminex.localization.internal;

import com.saravyasystems.filminex.localization.api.LocalizationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration(proxyBeanMethods = false)
class LocalizationConfiguration {

    @Bean
    LocalizationService localizationService(JdbcClient jdbcClient) {
        return new JdbcLocalizationService(jdbcClient);
    }
}
