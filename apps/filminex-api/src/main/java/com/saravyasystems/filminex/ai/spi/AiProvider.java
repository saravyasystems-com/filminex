package com.saravyasystems.filminex.ai.spi;

import com.saravyasystems.filminex.ai.api.AiProviderStatus;
import com.saravyasystems.filminex.ai.api.AiRequest;
import com.saravyasystems.filminex.ai.api.AiResult;

/** Adapter SPI. Business modules use AiService and never depend on this provider boundary. */
public interface AiProvider {

    AiProviderStatus status();

    AiResult execute(AiRequest request);
}
