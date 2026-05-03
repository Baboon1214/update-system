package com.example.update.service;

import com.example.update.model.AppVersion;
import com.example.update.repository.AppVersionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppVersionService {

    private final AppVersionRepository repository;

    public AppVersionService(AppVersionRepository repository) {
        this.repository = repository;
    }

    public AppVersion save(AppVersion version) {
        return repository.save(version);
    }

    public List<AppVersion> getAll() {
        return repository.findAll();
    }

    public AppVersion getLatest(String platform) {
        return repository
                .findTopByPlatformAndActiveTrueOrderByReleaseDateDesc(platform)
                .orElse(null);
    }
}