package com.example.storageapi;

import java.io.InputStream;

public interface StorageService {
    void store(String pkg, String version, String fileName, InputStream data) throws Exception;
    InputStream load(String pkg, String version, String fileName) throws Exception;
}
