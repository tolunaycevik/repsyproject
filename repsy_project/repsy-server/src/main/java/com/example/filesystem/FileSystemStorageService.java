package com.example.filesystem;

import com.example.storageapi.StorageService;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.*;

@Service
public class FileSystemStorageService implements StorageService {
    private final Path root = Paths.get("/data/repsy");

    @Override
    public void store(String pkg, String version, String fileName, InputStream data) throws Exception {
        Path dir = root.resolve(pkg).resolve(version);
        Files.createDirectories(dir);
        try (OutputStream out = Files.newOutputStream(dir.resolve(fileName), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            data.transferTo(out);
        }
    }

    @Override
    public InputStream load(String pkg, String version, String fileName) throws Exception {
        Path file = root.resolve(pkg).resolve(version).resolve(fileName);
        return Files.newInputStream(file, StandardOpenOption.READ);
    }
}
