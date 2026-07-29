package com.saravyasystems.filminex.rights.api;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record RightsGrant(
        UUID id,
        UUID workspaceId,
        UUID talentId,
        Set<RightsUse> uses,
        Set<String> territories,
        Instant validFrom,
        Instant validUntil,
        String evidenceReference,
        boolean revoked,
        UUID changedBy,
        Instant changedAt) {}
