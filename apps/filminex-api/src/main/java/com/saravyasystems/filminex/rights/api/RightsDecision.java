package com.saravyasystems.filminex.rights.api;

import java.util.List;

public record RightsDecision(
        RightsDecisionStatus status,
        RightsDecisionReason reason,
        String provider,
        List<String> evidenceReferences) {

    public RightsDecision {
        evidenceReferences =
                evidenceReferences == null ? List.of() : List.copyOf(evidenceReferences);
    }
}
