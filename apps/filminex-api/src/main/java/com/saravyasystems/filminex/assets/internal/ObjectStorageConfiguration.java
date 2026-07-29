package com.saravyasystems.filminex.assets.internal;

import com.saravyasystems.filminex.assets.api.ObjectStorage;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ObjectStorageConfiguration {

    @Bean
    ObjectStorage objectStorage(
            @Value("${filminex.storage.filesystem.root}") Path storageRoot) {
        return new FileSystemObjectStorage(storageRoot);
    }
}
