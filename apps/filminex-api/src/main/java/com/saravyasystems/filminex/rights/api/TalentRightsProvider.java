package com.saravyasystems.filminex.rights.api;

import java.util.List;
import java.util.UUID;

/** Replaceable public boundary for operational talent-rights decisions. */
public interface TalentRightsProvider {

    RightsDecision evaluate(RightsRequest request);

    RightsGrant grant(LocalRightsGrant grant);

    void revoke(UUID workspaceId, UUID actorUserId, UUID grantId);

    List<RightsGrant> list(UUID workspaceId, UUID actorUserId, UUID talentId);
}
