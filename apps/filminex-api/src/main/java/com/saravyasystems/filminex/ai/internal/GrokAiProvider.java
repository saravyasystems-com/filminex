package com.saravyasystems.filminex.ai.internal;

import com.saravyasystems.filminex.ai.api.AiProviderException;
import com.saravyasystems.filminex.ai.api.AiProviderStatus;
import com.saravyasystems.filminex.ai.api.AiRequest;
import com.saravyasystems.filminex.ai.api.AiResult;
import com.saravyasystems.filminex.ai.spi.AiProvider;

/** Grok-specific adapter boundary. Network transport is deliberately deferred. */
final class GrokAiProvider implements AiProvider {

    private final String model;
    private final boolean configured;

    GrokAiProvider(String model, String apiKey) {
        this.model = model;
        this.configured = apiKey != null && !apiKey.isBlank();
    }

    @Override
    public AiProviderStatus status() {
        return new AiProviderStatus("grok", model, false);
    }

    @Override
    public AiResult execute(AiRequest request) {
        String state = configured ? "transport is not enabled" : "credentials are not configured";
        throw new AiProviderException("Grok provider unavailable: " + state);
    }
}
