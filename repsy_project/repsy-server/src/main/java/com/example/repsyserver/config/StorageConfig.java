package com.example.repsyserver.config;

import com.example.filesystem.FileSystemStorageService;
import com.example.minio.MinioStorageService;
import com.example.storageapi.StorageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;    // ← import this

@Configuration
public class StorageConfig {
    @Bean
    @Primary                                      // ← add this
    @ConditionalOnProperty(name = "storage.strategy", havingValue = "file-system", matchIfMissing = true)
    public StorageService fileSystem(FileSystemStorageService svc) {
        return svc;
    }

    @Bean
    @ConditionalOnProperty(name = "storage.strategy", havingValue = "object-storage")
    public StorageService minio(MinioStorageService svc) {
        return svc;
    }
}
