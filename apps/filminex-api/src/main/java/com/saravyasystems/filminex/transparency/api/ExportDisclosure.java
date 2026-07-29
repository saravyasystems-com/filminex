package com.saravyasystems.filminex.transparency.api;

import java.util.Optional;

/** Stable export instruction without freezing artwork or a metadata encoding. */
public record ExportDisclosure(
        TransparencyDecision decision,
        DisclosureMethod method,
        Optional<String> categoryId) {

    public ExportDisclosure {
        categoryId = categoryId == null ? Optional.empty() : categoryId;
        if (decision.required() != categoryId.isPresent()) {
            throw new IllegalArgumentException("Required disclosures must name an MTI category");
        }
        if (decision.required() && method == DisclosureMethod.NONE) {
            throw new IllegalArgumentException("Required decisions must emit a disclosure");
        }
        if (!decision.required() && method != DisclosureMethod.NONE) {
            throw new IllegalArgumentException("Non-applicable decisions cannot emit a disclosure");
        }
    }
}
