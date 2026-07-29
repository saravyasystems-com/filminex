package com.saravyasystems.filminex.transparency.internal;

import com.saravyasystems.filminex.audit.api.AuditActorType;
import com.saravyasystems.filminex.audit.api.AuditEvent;
import com.saravyasystems.filminex.audit.api.AuditOutcome;
import com.saravyasystems.filminex.audit.api.AuditService;
import com.saravyasystems.filminex.transparency.api.DisclosureMethod;
import com.saravyasystems.filminex.transparency.api.ExportDisclosure;
import com.saravyasystems.filminex.transparency.api.MediaKind;
import com.saravyasystems.filminex.transparency.api.MediaTransparencyService;
import com.saravyasystems.filminex.transparency.api.TransparencyDecision;
import com.saravyasystems.filminex.transparency.api.TransparencyProductionMode;
import com.saravyasystems.filminex.transparency.api.TransparencyReason;
import com.saravyasystems.filminex.transparency.api.TransparencyRequest;
import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

class DefaultMediaTransparencyService implements MediaTransparencyService {

    static final String DEEPFAKE_ICON_CATEGORY = "MTI-001";

    private final AuditService audit;
    private final Clock clock;

    DefaultMediaTransparencyService(AuditService audit, Clock clock) {
        this.audit = audit;
        this.clock = clock;
    }

    @Override
    public TransparencyDecision evaluate(TransparencyRequest request) {
        TransparencyDecision decision = decide(request);
        appendAudit(request, decision);
        return decision;
    }

    @Override
    public ExportDisclosure disclosureFor(TransparencyRequest request) {
        TransparencyDecision decision = evaluate(request);
        if (!decision.required()) {
            return new ExportDisclosure(decision, DisclosureMethod.NONE, Optional.empty());
        }
        DisclosureMethod method =
                request.mediaKind() == MediaKind.AUDIO
                        ? DisclosureMethod.METADATA
                        : DisclosureMethod.ICON;
        return new ExportDisclosure(
                decision, method, Optional.of(DEEPFAKE_ICON_CATEGORY));
    }

    private static TransparencyDecision decide(TransparencyRequest request) {
        if (request.productionMode() != TransparencyProductionMode.AI
                && request.productionMode() != TransparencyProductionMode.HYBRID) {
            return notRequired(TransparencyReason.NON_AI_PRODUCTION);
        }
        if (!request.realHumanLikeness() && !request.realHumanVoice()) {
            return notRequired(TransparencyReason.NO_REAL_HUMAN_LIKENESS_OR_VOICE);
        }
        if (!request.syntheticOrMateriallyAltered()) {
            return notRequired(TransparencyReason.NOT_SYNTHETIC_OR_MATERIALLY_ALTERED);
        }
        return new TransparencyDecision(
                true,
                TransparencyReason.APPLICABLE_SYNTHETIC_HUMAN_MEDIA,
                request.evidenceReferences());
    }

    private static TransparencyDecision notRequired(TransparencyReason reason) {
        return new TransparencyDecision(false, reason, java.util.List.of());
    }

    private void appendAudit(
            TransparencyRequest request, TransparencyDecision decision) {
        audit.append(new AuditEvent(
                request.workspaceId(),
                AuditActorType.USER,
                request.actorId(),
                "transparency.mti-evaluated",
                "media",
                request.mediaReference(),
                AuditOutcome.SUCCEEDED,
                Instant.now(clock),
                request.correlationId(),
                request.id(),
                Map.of(
                        "category", DEEPFAKE_ICON_CATEGORY,
                        "mediaKind", request.mediaKind().name(),
                        "required", Boolean.toString(decision.required()),
                        "reason", decision.reason().name())));
    }
}
