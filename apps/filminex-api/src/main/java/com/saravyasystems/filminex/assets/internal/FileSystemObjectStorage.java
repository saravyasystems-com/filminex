package com.saravyasystems.filminex.assets.internal;

import com.saravyasystems.filminex.assets.api.ObjectKey;
import com.saravyasystems.filminex.assets.api.ObjectStorage;
import com.saravyasystems.filminex.assets.api.StoredObject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Optional;

final class FileSystemObjectStorage implements ObjectStorage {

    private final Path root;

    FileSystemObjectStorage(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    @Override
    public StoredObject put(ObjectKey key, InputStream content) {
        Path target = resolve(key);
        try {
            Files.createDirectories(target.getParent());
            Path temporary = Files.createTempFile(target.getParent(), ".upload-", ".tmp");
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                try (InputStream source = new BufferedInputStream(content);
                        DigestInputStream hashingSource = new DigestInputStream(source, digest);
                        OutputStream destination = Files.newOutputStream(temporary)) {
                    hashingSource.transferTo(destination);
                }
                Files.move(temporary, target);
                return metadata(key, target, HexFormat.of().formatHex(digest.digest()));
            } finally {
                Files.deleteIfExists(temporary);
            }
        } catch (IOException | NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Could not store object " + key.value(), exception);
        }
    }

    @Override
    public Optional<StoredObject> stat(ObjectKey key) {
        Path target = resolve(key);
        if (!Files.isRegularFile(target)) {
            return Optional.empty();
        }
        try (InputStream content = Files.newInputStream(target)) {
            return Optional.of(metadata(key, target, sha256(content)));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not inspect object " + key.value(), exception);
        }
    }

    @Override
    public Optional<InputStream> get(ObjectKey key) {
        Path target = resolve(key);
        if (!Files.isRegularFile(target)) {
            return Optional.empty();
        }
        try {
            return Optional.of(Files.newInputStream(target));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not read object " + key.value(), exception);
        }
    }

    @Override
    public boolean delete(ObjectKey key) {
        try {
            return Files.deleteIfExists(resolve(key));
        } catch (IOException exception) {
            throw new IllegalStateException("Could not delete object " + key.value(), exception);
        }
    }

    private Path resolve(ObjectKey key) {
        Path target = root.resolve(key.value()).normalize();
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("Object key escapes storage root");
        }
        return target;
    }

    private static StoredObject metadata(ObjectKey key, Path target, String sha256)
            throws IOException {
        BasicFileAttributes attributes =
                Files.readAttributes(target, BasicFileAttributes.class);
        return new StoredObject(
                key,
                attributes.size(),
                sha256,
                Instant.ofEpochMilli(attributes.lastModifiedTime().toMillis()));
    }

    private static String sha256(InputStream content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (DigestInputStream hashingSource = new DigestInputStream(content, digest)) {
                hashingSource.transferTo(OutputStream.nullOutputStream());
            }
            return HexFormat.of().formatHex(digest.digest());
        } catch (IOException | NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Could not calculate object checksum", exception);
        }
    }
}
