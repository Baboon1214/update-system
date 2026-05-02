package com.example.update.controller;

import com.example.update.dto.UpdateResponse;
import com.example.update.model.AppVersion;
import com.example.update.repository.AppVersionRepository;
import org.springframework.web.bind.annotation.*;
import com.example.update.util.VersionComparator;




@RestController
@RequestMapping("/api/update")
public class UpdateController {

    private final AppVersionRepository repository;

    public UpdateController(AppVersionRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/check")
    public UpdateResponse checkUpdate(
            @RequestParam String current,
            @RequestParam String platform
    ) {

        AppVersion latest = repository
                .findTopByPlatformAndActiveTrueOrderByReleaseDateDesc(platform);

        UpdateResponse response = new UpdateResponse();

        if (latest == null) {
            response.setUpdateAvailable(false);
            response.setLatestVersion(current);
            return response;
        }

        response.setLatestVersion(latest.getVersion());
        response.setUpdateType(latest.getUpdateType());
        response.setChangelog(latest.getChangelog());

        response.setUpdateAvailable(
        VersionComparator.isNewer(latest.getVersion(), current)
);

        return response;
    }
}