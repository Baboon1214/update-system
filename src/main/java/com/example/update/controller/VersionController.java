package com.example.update.controller;

import com.example.update.dto.AppVersionRequest;
import com.example.update.model.AppVersion;
import com.example.update.repository.AppVersionRepository;
import com.example.update.service.TelegramNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final TelegramNotificationService telegramService;

    // Исправленный конструктор с двумя зависимостями
    public VersionController(AppVersionRepository repository, TelegramNotificationService telegramService) {
        this.repository = repository;
        this.telegramService = telegramService;
    }

    @PostMapping
    @Operation(summary = "Создать новую версию")
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

        // Отправка уведомления в Telegram
        String message = String.format(
            "📢 Новая версия приложения!\nВерсия: %s\nПлатформа: %s\nТип: %s\nОписание: %s",
            saved.getVersion(),
            saved.getPlatform(),
            saved.getUpdateType(),
            saved.getChangelog()
        );
        telegramService.sendMessage(message);

        return ResponseEntity.ok(saved);
    }

    @GetMapping
    @Operation(summary = "Получить все версии")
    public List<AppVersion> getAll() {
        log.debug("Fetching all versions");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить версию по ID")
    public AppVersion getById(@PathVariable Long id) {
        log.debug("Fetching version by id: {}", id);
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Version not found"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить версию")
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
    @Operation(summary = "Удалить версию")
    public void delete(@PathVariable Long id) {
        log.warn("Deleting version id: {}", id);
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Version not found");
        }
        repository.deleteById(id);
        log.info("Version deleted: {}", id);
    }

    @GetMapping("/latest")
    @Operation(summary = "Последняя версия для платформы")
    public ResponseEntity<AppVersion> getLatest(@RequestParam String platform) {
        log.info("Getting latest version for platform: {}", platform);
        Optional<AppVersion> latest = repository.findTopByPlatformAndActiveTrueOrderByReleaseDateDesc(platform);
        return latest.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}