package com.saravyasystems.filminex.assets.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.saravyasystems.filminex.assets.api.ObjectKey;
import com.saravyasystems.filminex.assets.api.ObjectStorage;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FileSystemObjectStorageTests {

    @TempDir Path storageRoot;

    @Test
    void storesReadsInspectsAndDeletesBinaryContent() throws Exception {
        ObjectStorage storage = new FileSystemObjectStorage(storageRoot);
        ObjectKey key = new ObjectKey("workspace-1/assets/source.mov");
        byte[] content = "filminex-media".getBytes(StandardCharsets.UTF_8);

        var stored = storage.put(key, new ByteArrayInputStream(content));

        assertThat(stored.key()).isEqualTo(key);
        assertThat(stored.size()).isEqualTo(content.length);
        assertThat(stored.sha256())
                .isEqualTo("5c298d335a5c378764dccf0007756bd9759e2ebf04fa3831b918d3f3fac9c797");
        assertThat(storage.stat(key)).contains(stored);
        try (var source = storage.get(key).orElseThrow()) {
            assertThat(source.readAllBytes()).isEqualTo(content);
        }
        assertThatThrownBy(() -> storage.put(key, new ByteArrayInputStream(content)))
                .isInstanceOf(IllegalStateException.class);
        assertThat(storage.delete(key)).isTrue();
        assertThat(storage.get(key)).isEmpty();
    }

    @Test
    void rejectsTraversalKeys() {
        assertThatThrownBy(() -> new ObjectKey("../secrets"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
