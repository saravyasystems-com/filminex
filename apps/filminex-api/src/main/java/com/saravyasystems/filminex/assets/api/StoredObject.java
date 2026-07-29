package com.saravyasystems.filminex.assets.api;

import java.time.Instant;

/** Storage result suitable for persistence as authoritative asset metadata. */
public record StoredObject(ObjectKey key, long size, String sha256, Instant storedAt) {}
