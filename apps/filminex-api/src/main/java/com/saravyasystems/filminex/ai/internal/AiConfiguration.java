package com.saravyasystems.filminex.ai.internal;

import com.saravyasystems.filminex.ai.api.AiService;
import com.saravyasystems.filminex.ai.spi.AiProvider;
import com.saravyasystems.filminex.audit.api.AuditService;
import com.saravyasystems.filminex.capabilities.api.CapabilityService;
import java.time.Clock;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AiProperties.class)
class AiConfiguration {

    @Bean
    Clock aiClock() {
        return Clock.systemUTC();
    }

    @Bean
    AiProvider aiProvider(AiProperties properties, Clock aiClock) {
        return switch (properties.provider().toLowerCase()) {
            case "local" -> new LocalAiProvider(aiClock);
            case "grok" -> new GrokAiProvider(properties.model(), properties.grokApiKey());
            default -> throw new IllegalArgumentException(
                    "Unsupported AI provider: " + properties.provider());
        };
    }

    @Bean
    AiService aiService(
            AiProvider provider,
            CapabilityService capabilities,
            AuditService audit,
            Clock aiClock) {
        return new DefaultAiService(provider, capabilities, audit, aiClock);
    }
}
