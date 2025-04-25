package com.example.repsyserver.repo;

import com.example.repsyserver.domain.PackageMeta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PackageMetaRepository extends JpaRepository<PackageMeta, Long> {
    Optional<PackageMeta> findByNameAndVersion(String name, String version);
}
