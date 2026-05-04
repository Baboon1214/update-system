package com.example.update.controller;

import com.example.update.dto.UpdateResponse;
import com.example.update.model.AppVersion;
import com.example.update.repository.AppVersionRepository;
import com.example.update.repository.UserDeviceRepository;
import com.example.update.util.VersionComparator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/update")
@SecurityRequirement(name = "bearerAuth")
public class UpdateController {

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
            @RequestParam String platform
    ) {
        // (Здесь можно обновлять lastSeen пользователя, если нужно)

        Optional<AppVersion> latestOpt = versionRepository
                .findTopByPlatformAndActiveTrueOrderByReleaseDateDesc(platform);

        UpdateResponse response = new UpdateResponse();

        if (latestOpt.isEmpty()) {
            response.setUpdateAvailable(false);
            response.setLatestVersion(current);
            return response;
        }

        AppVersion latest = latestOpt.get();

        response.setLatestVersion(latest.getVersion());
        response.setUpdateType(latest.getUpdateType());          // теперь это String
        response.setChangelog(latest.getChangelog());
        response.setForceUpdate("MANDATORY".equals(latest.getUpdateType())); // сравнение строк
        response.setUpdateAvailable(VersionComparator.isNewer(latest.getVersion(), current));

        return response;
    }
}