package com.saravyasystems.filminex.ai.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.saravyasystems.filminex.ai.api.AiProviderException;
import com.saravyasystems.filminex.ai.api.AiProviderStatus;
import com.saravyasystems.filminex.ai.api.AiRequest;
import com.saravyasystems.filminex.ai.api.AiTaskType;
import com.saravyasystems.filminex.capabilities.api.ProductionMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GrokAiProviderTests {

    @Test
    void remainsUnavailableUntilTheApprovedTransportIsEnabled() {
        GrokAiProvider grok = new GrokAiProvider("grok-model", "configured-key");

        assertThat(grok.status())
                .isEqualTo(new AiProviderStatus("grok", "grok-model", false));
        assertThatThrownBy(() -> grok.execute(request()))
                .isInstanceOf(AiProviderException.class)
                .hasMessageContaining("transport is not enabled");
    }

    @Test
    void reportsMissingCredentialsWithoutExposingASecret() {
        GrokAiProvider grok = new GrokAiProvider("grok-model", "");

        assertThatThrownBy(() -> grok.execute(request()))
                .isInstanceOf(AiProviderException.class)
                .hasMessageContaining("credentials are not configured")
                .hasMessageNotContaining("api-key");
    }

    private AiRequest request() {
        UUID id = UUID.randomUUID();
        return new AiRequest(
                id,
                UUID.randomUUID(),
                UUID.randomUUID(),
                AiTaskType.TEXT_GENERATION,
                ProductionMode.AI,
                "Generate a proposal",
                "",
                List.of(),
                Map.of(),
                id);
    }
}
