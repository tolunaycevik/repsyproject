package com.example.repsyserver.controller;

import com.example.repsyserver.domain.PackageMeta;
import com.example.repsyserver.repo.PackageMetaRepository;
import com.example.storageapi.StorageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@RestController
@RequestMapping("/")
public class PackageController {
    private final PackageMetaRepository repo;
    private final StorageService storage;

    public PackageController(PackageMetaRepository repo, StorageService storage) {
        this.repo = repo;
        this.storage = storage;
    }

    @PostMapping("{name}/{version}")
    public ResponseEntity<?> upload(
        @PathVariable String name,
        @PathVariable String version,
        @RequestPart("package.rep") MultipartFile rep,
        @RequestPart("meta.json") MultipartFile metaJson) throws Exception {
        
        PackageMeta meta = new PackageMeta();
        meta.setName(name);
        meta.setVersion(version);
        meta.setAuthor("Author");
        meta.setUploadedAt(Instant.now());
        repo.save(meta);
        
        storage.store(name, version, "package.rep", rep.getInputStream());
        storage.store(name, version, "meta.json", metaJson.getInputStream());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("{name}/{version}/{fileName}")
    public ResponseEntity<?> download(
        @PathVariable String name,
        @PathVariable String version,
        @PathVariable String fileName) throws Exception {
        
        if (!repo.findByNameAndVersion(name, version).isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
        InputStreamResource resource = new InputStreamResource(
            storage.load(name, version, fileName));
        MediaType mt = fileName.endsWith(".json") 
            ? MediaType.APPLICATION_JSON 
            : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
            .contentType(mt)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .body(resource);
    }
}
