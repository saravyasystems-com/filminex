package com.saravyasystems.filminex.ai.internal;

import com.saravyasystems.filminex.ai.api.AiAccessDeniedException;
import com.saravyasystems.filminex.ai.api.AiProviderStatus;
import com.saravyasystems.filminex.ai.api.AiRequest;
import com.saravyasystems.filminex.ai.api.AiResult;
import com.saravyasystems.filminex.ai.api.AiService;
import com.saravyasystems.filminex.ai.spi.AiProvider;
import com.saravyasystems.filminex.audit.api.AuditActorType;
import com.saravyasystems.filminex.audit.api.AuditEvent;
import com.saravyasystems.filminex.audit.api.AuditOutcome;
import com.saravyasystems.filminex.audit.api.AuditService;
import com.saravyasystems.filminex.capabilities.api.Capability;
import com.saravyasystems.filminex.capabilities.api.CapabilityDecision;
import com.saravyasystems.filminex.capabilities.api.CapabilityRequest;
import com.saravyasystems.filminex.capabilities.api.CapabilityService;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;

final class DefaultAiService implements AiService {

    private final AiProvider provider;
    private final CapabilityService capabilities;
    private final AuditService audit;
    private final Clock clock;

    DefaultAiService(
            AiProvider provider, CapabilityService capabilities, AuditService audit, Clock clock) {
        this.provider = provider;
        this.capabilities = capabilities;
        this.audit = audit;
        this.clock = clock;
    }

    @Override
    public AiResult execute(AiRequest request) {
        AiProviderStatus status = provider.status();
        CapabilityDecision decision = capabilities.evaluate(new CapabilityRequest(
                request.workspaceId(),
                request.requestedBy(),
                Capability.AI_STUDIO,
                request.productionMode(),
                status.available()));
        if (!decision.allowed()) {
            appendAudit(request, AuditOutcome.DENIED, status, decision.reason().name());
            throw new AiAccessDeniedException(decision.reason());
        }

        try {
            AiResult result = provider.execute(request);
            appendAudit(request, AuditOutcome.SUCCEEDED, status, result.finishReason());
            return result;
        } catch (RuntimeException exception) {
            appendAudit(request, AuditOutcome.FAILED, status, exception.getClass().getSimpleName());
            throw exception;
        }
    }

    @Override
    public AiProviderStatus providerStatus() {
        return provider.status();
    }

    private void appendAudit(
            AiRequest request,
            AuditOutcome outcome,
            AiProviderStatus status,
            String result) {
        audit.append(new AuditEvent(
                request.workspaceId(),
                AuditActorType.USER,
                request.requestedBy().toString(),
                "ai.request",
                "ai-request",
                request.id().toString(),
                outcome,
                Instant.now(clock),
                request.correlationId(),
                null,
                Map.of(
                        "provider", status.provider(),
                        "model", status.model(),
                        "taskType", request.taskType().name(),
                        "result", result)));
    }
}
