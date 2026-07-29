package com.saravyasystems.filminex.ai.api;

/** Provider-neutral public boundary for AI tasks. */
public interface AiService {

    AiResult execute(AiRequest request);

    AiProviderStatus providerStatus();
}
