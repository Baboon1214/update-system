package com.example.update.controller;

import com.example.update.dto.UpdateResponse;
import com.example.update.model.AppVersion;
import com.example.update.model.UserDevice;
import com.example.update.repository.AppVersionRepository;
import com.example.update.repository.UserDeviceRepository;
import com.example.update.util.VersionComparator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/update")
@SecurityRequirement(name = "bearerAuth")
public class UpdateController {

    private static final Logger log = LoggerFactory.getLogger(UpdateController.class);

    private final AppVersionRepository versionRepository;
    private final UserDeviceRepository deviceRepository;

    public UpdateController(AppVersionRepository versionRepository, UserDeviceRepository deviceRepository) {
        this.versionRepository = versionRepository;
        this.deviceRepository = deviceRepository;
    }

    @GetMapping("/check")
    @Operation(summary = "Проверка обновления")
    public UpdateResponse checkUpdate(
            @RequestParam String userId,
            @RequestParam String current,
            @RequestParam String platform) {

        log.info("Check update: userId={}, current={}, platform={}", userId, current, platform);

        Optional<AppVersion> latestOpt = versionRepository
                .findTopByPlatformAndActiveTrueOrderByReleaseDateDesc(platform);

        UpdateResponse response = new UpdateResponse();

        if (latestOpt.isEmpty()) {
            log.warn("No active version for platform: {}", platform);
            response.setUpdateAvailable(false);
            response.setLatestVersion(current);
            return response;
        }

        AppVersion latest = latestOpt.get();
        response.setLatestVersion(latest.getVersion());
        response.setUpdateType(latest.getUpdateType());
        response.setChangelog(latest.getChangelog());
        response.setForceUpdate("MANDATORY".equals(latest.getUpdateType()));
        response.setUpdateAvailable(VersionComparator.isNewer(latest.getVersion(), current));

        log.info("Result: available={}, latest={}", response.isUpdateAvailable(), response.getLatestVersion());
        return response;
    }

    @PostMapping("/log")
    @Operation(summary = "Лог установки обновления")
    public String logUpdate(@RequestParam String userId,
                            @RequestParam String platform,
                            @RequestParam String newVersion) {
        log.info("Log update: userId={}, platform={}, newVersion={}", userId, platform, newVersion);

        int updated = deviceRepository.updateUserVersion(userId, platform, newVersion, LocalDateTime.now());

        if (updated > 0) {
            log.info("Update logged for user {}", userId);
            return "Update logged successfully";
        } else {
            log.warn("Device not found, creating new for user {}", userId);
            UserDevice device = new UserDevice(userId, platform, newVersion);
            device.setLastUpdateLog(LocalDateTime.now());
            deviceRepository.save(device);
            return "New device created with update log";
        }
    }
}