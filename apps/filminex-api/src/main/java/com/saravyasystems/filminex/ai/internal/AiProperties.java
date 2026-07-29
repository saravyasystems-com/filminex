package com.saravyasystems.filminex.ai.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("filminex.ai")
record AiProperties(String provider, String model, String grokApiKey) {

    AiProperties {
        provider = provider == null || provider.isBlank() ? "local" : provider;
        model = model == null || model.isBlank() ? "grok-default" : model;
        grokApiKey = grokApiKey == null ? "" : grokApiKey;
    }
}
