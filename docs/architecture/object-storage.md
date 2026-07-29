# Object Storage

## Boundary

`assets.api.ObjectStorage` is the only application-facing binary-storage contract. It uses
vendor-neutral object keys and returns size, SHA-256 checksum, and storage time for persistence
as authoritative asset metadata.

PostgreSQL remains the source of truth for asset identity, ownership, versions, provenance, and
object references. The storage adapter owns binary transfer only.

## Local-development adapter

`FileSystemObjectStorage` writes beneath `FILMINEX_STORAGE_ROOT`, which defaults to
`./var/filminex/objects`. It:

- creates nested key directories;
- streams content instead of retaining complete media in memory;
- computes SHA-256 during upload;
- atomically publishes a new immutable target after a successful write;
- prevents path traversal outside the configured root;
- supports read, metadata inspection, and deletion.

The filesystem adapter proves the seam without selecting a production object-storage vendor.
A future S3-compatible or cloud-native adapter must implement the same public contract and remain
inside the assets module.

## Operational verification

Run:

```bash
./gradlew test
```

`FileSystemObjectStorageTests` proves the complete storage lifecycle in an isolated temporary
directory. No developer media or workstation path enters the test.
