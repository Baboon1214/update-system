package com.example.update.controller;

import com.example.update.dto.AppVersionRequest;
import com.example.update.model.AppVersion;
import com.example.update.repository.AppVersionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/versions")
@SecurityRequirement(name = "bearerAuth")
public class VersionController {

    private static final Logger log = LoggerFactory.getLogger(VersionController.class);
    private final AppVersionRepository repository;

    public VersionController(AppVersionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    @Operation(summary = "Получить все версии", description = "Доступно всем авторизованным пользователям")
    public List<AppVersion> getAll() {
        log.debug("Fetching all versions");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить версию по ID", description = "Доступно всем авторизованным пользователям")
    public AppVersion getById(@PathVariable Long id) {
        log.debug("Fetching version by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Version not found"));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Создать новую версию (только для ADMIN)")
    public ResponseEntity<AppVersion> create(@Valid @RequestBody AppVersionRequest request) {
        log.info("Creating new version: {} for platform {}", request.getVersion(), request.getPlatform());

        AppVersion version = new AppVersion();
        version.setVersion(request.getVersion());
        version.setPlatform(request.getPlatform());
        version.setChangelog(request.getChangelog());
        version.setUpdateType(request.getUpdateType());
        version.setActive(request.isActive());

        AppVersion saved = repository.save(version);
        log.info("Version created with id: {}", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Обновить версию (только для ADMIN)")
    public AppVersion update(@PathVariable Long id, @Valid @RequestBody AppVersionRequest request) {
        log.info("Updating version id: {}", id);
        AppVersion version = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Version not found"));

        version.setVersion(request.getVersion());
        version.setPlatform(request.getPlatform());
        version.setChangelog(request.getChangelog());
        version.setUpdateType(request.getUpdateType());
        version.setActive(request.isActive());

        AppVersion updated = repository.save(version);
        log.info("Version updated: {}", id);
        return updated;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить версию (только для ADMIN)")
    public void delete(@PathVariable Long id) {
        log.warn("Deleting version id: {}", id);
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Version not found");
        }
        repository.deleteById(id);
        log.info("Version deleted: {}", id);
    }

    @GetMapping("/latest")
    @Operation(summary = "Последняя версия для платформы", description = "Доступно всем авторизованным пользователям")
    public ResponseEntity<AppVersion> getLatest(@RequestParam String platform) {
        log.info("Getting latest version for platform: {}", platform);
        Optional<AppVersion> latest = repository.findTopByPlatformAndActiveTrueOrderByReleaseDateDesc(platform);
        return latest.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}