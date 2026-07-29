package com.saravyasystems.filminex.ai.internal;

import com.saravyasystems.filminex.ai.api.AiProviderStatus;
import com.saravyasystems.filminex.ai.api.AiRequest;
import com.saravyasystems.filminex.ai.api.AiResult;
import com.saravyasystems.filminex.ai.api.AiUsage;
import com.saravyasystems.filminex.ai.spi.AiProvider;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;

/** Deterministic development adapter; it never calls an external AI service. */
final class LocalAiProvider implements AiProvider {

    private final Clock clock;

    LocalAiProvider(Clock clock) {
        this.clock = clock;
    }

    @Override
    public AiProviderStatus status() {
        return new AiProviderStatus("local", "deterministic", true);
    }

    @Override
    public AiResult execute(AiRequest request) {
        return new AiResult(
                request.id(),
                "local",
                "deterministic",
                "LOCAL_PROPOSAL: " + request.instruction(),
                "completed",
                Map.of("reviewRequired", "true"),
                new AiUsage(request.instruction().length(), 0),
                Instant.now(clock));
    }
}
