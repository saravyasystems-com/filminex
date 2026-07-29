package com.saravyasystems.filminex.transparency.api;

import java.util.List;

/** Explainable MTI applicability result. */
public record TransparencyDecision(
        boolean required, TransparencyReason reason, List<String> evidenceReferences) {

    public TransparencyDecision {
        evidenceReferences =
                evidenceReferences == null ? List.of() : List.copyOf(evidenceReferences);
    }
}
