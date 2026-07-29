package com.saravyasystems.filminex.assets.api;

import java.io.InputStream;
import java.util.Optional;

/** Vendor-neutral public boundary for immutable media binaries. */
public interface ObjectStorage {

    StoredObject put(ObjectKey key, InputStream content);

    Optional<StoredObject> stat(ObjectKey key);

    Optional<InputStream> get(ObjectKey key);

    boolean delete(ObjectKey key);
}
