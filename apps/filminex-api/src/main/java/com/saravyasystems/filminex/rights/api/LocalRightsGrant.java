package com.saravyasystems.filminex.rights.api;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public record LocalRightsGrant(
        UUID workspaceId,
        UUID actorUserId,
        UUID talentId,
        Set<RightsUse> uses,
        Set<String> territories,
        Instant validFrom,
        Instant validUntil,
        String evidenceReference) {

    public LocalRightsGrant {
        Objects.requireNonNull(workspaceId, "workspaceId");
        Objects.requireNonNull(actorUserId, "actorUserId");
        Objects.requireNonNull(talentId, "talentId");
        uses = Set.copyOf(uses);
        territories = Set.copyOf(territories);
        Objects.requireNonNull(validFrom, "validFrom");
        Objects.requireNonNull(validUntil, "validUntil");
        if (!validUntil.isAfter(validFrom)) {
            throw new IllegalArgumentException("validUntil must be after validFrom");
        }
        if (evidenceReference == null || evidenceReference.isBlank()) {
            throw new IllegalArgumentException("evidenceReference must not be blank");
        }
    }
}
