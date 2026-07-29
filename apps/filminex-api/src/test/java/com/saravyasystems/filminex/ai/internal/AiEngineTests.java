package com.saravyasystems.filminex.ai.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.saravyasystems.filminex.ai.api.AiAccessDeniedException;
import com.saravyasystems.filminex.ai.api.AiProviderException;
import com.saravyasystems.filminex.ai.api.AiProviderStatus;
import com.saravyasystems.filminex.ai.api.AiRequest;
import com.saravyasystems.filminex.ai.api.AiResult;
import com.saravyasystems.filminex.ai.api.AiTaskType;
import com.saravyasystems.filminex.ai.api.AiUsage;
import com.saravyasystems.filminex.ai.spi.AiProvider;
import com.saravyasystems.filminex.audit.api.AuditEvent;
import com.saravyasystems.filminex.audit.api.AuditOutcome;
import com.saravyasystems.filminex.audit.api.AuditService;
import com.saravyasystems.filminex.capabilities.api.CapabilityDecision;
import com.saravyasystems.filminex.capabilities.api.CapabilityDecisionReason;
import com.saravyasystems.filminex.capabilities.api.CapabilityService;
import com.saravyasystems.filminex.capabilities.api.ProductionMode;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AiEngineTests {

    private static final Instant NOW = Instant.parse("2026-07-30T00:00:00Z");

    @Mock
    private AiProvider provider;

    @Mock
    private CapabilityService capabilities;

    @Mock
    private AuditService audit;

    private DefaultAiService service;

    @BeforeEach
    void setUp() {
        service = new DefaultAiService(
                provider, capabilities, audit, Clock.fixed(NOW, ZoneOffset.UTC));
        lenient()
                .when(provider.status())
                .thenReturn(new AiProviderStatus("test", "model", true));
    }

    @Test
    void routesAllowedRequestsThroughProviderAndPreservesNormalizedProvenance() {
        AiRequest request = request();
        AiResult result = new AiResult(
                request.id(),
                "test",
                "model",
                "proposal",
                "completed",
                Map.of("reviewRequired", "true"),
                new AiUsage(12, 4),
                NOW);
        when(capabilities.evaluate(any())).thenReturn(CapabilityDecision.allow());
        when(provider.execute(request)).thenReturn(result);

        assertThat(service.execute(request)).isEqualTo(result);

        AuditEvent event = capturedAudit();
        assertThat(event.outcome()).isEqualTo(AuditOutcome.SUCCEEDED);
        assertThat(event.details())
                .containsEntry("provider", "test")
                .containsEntry("model", "model")
                .containsEntry("taskType", "STRUCTURED_PROPOSAL");
    }

    @Test
    void deniedCapabilityNeverCallsProviderAndIsAudited() {
        when(capabilities.evaluate(any()))
                .thenReturn(CapabilityDecision.deny(
                        CapabilityDecisionReason.ENTITLEMENT_REQUIRED));

        assertThatThrownBy(() -> service.execute(request()))
                .isInstanceOf(AiAccessDeniedException.class)
                .hasMessageContaining("ENTITLEMENT_REQUIRED");
        verify(provider, never()).execute(any());
        assertThat(capturedAudit().outcome()).isEqualTo(AuditOutcome.DENIED);
    }

    @Test
    void providerFailureIsNormalizedAtBoundaryAndAudited() {
        when(capabilities.evaluate(any())).thenReturn(CapabilityDecision.allow());
        when(provider.execute(any())).thenThrow(new AiProviderException("temporary failure"));

        assertThatThrownBy(() -> service.execute(request()))
                .isInstanceOf(AiProviderException.class)
                .hasMessageContaining("temporary failure");
        assertThat(capturedAudit().outcome()).isEqualTo(AuditOutcome.FAILED);
    }

    @Test
    void localAdapterIsDeterministicAndRequiresReview() {
        LocalAiProvider local = new LocalAiProvider(Clock.fixed(NOW, ZoneOffset.UTC));

        AiResult result = local.execute(request());

        assertThat(result.provider()).isEqualTo("local");
        assertThat(result.output()).startsWith("LOCAL_PROPOSAL:");
        assertThat(result.safetyMetadata()).containsEntry("reviewRequired", "true");
    }

    private AuditEvent capturedAudit() {
        ArgumentCaptor<AuditEvent> captor = ArgumentCaptor.forClass(AuditEvent.class);
        verify(audit).append(captor.capture());
        return captor.getValue();
    }

    private AiRequest request() {
        UUID requestId = UUID.randomUUID();
        return new AiRequest(
                requestId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                AiTaskType.STRUCTURED_PROPOSAL,
                ProductionMode.HYBRID,
                "Propose a scene breakdown",
                "No continuity changes",
                List.of("workspace/reference.png"),
                Map.of("depth", "scene"),
                requestId);
    }
}
