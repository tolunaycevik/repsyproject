package com.example.minio;

import com.example.storageapi.StorageService;
import java.io.InputStream;
import org.springframework.stereotype.Service;    // ← import this

@Service                                   // ← add this
public class MinioStorageService implements StorageService {
    @Override
    public void store(String pkg, String v, String name, InputStream in) {
    }
    @Override
    public InputStream load(String pkg, String v, String name) {
        return InputStream.nullInputStream();
    }
}
