package com.example.update.controller;

import com.example.update.model.AppVersion;
import com.example.update.repository.AppVersionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/versions")
public class VersionController {

    private final AppVersionRepository repository;

    public VersionController(AppVersionRepository repository) {
        this.repository = repository;
    }

    // ➜ СОХРАНЕНИЕ ВЕРСИИ
    @PostMapping
public AppVersion create(@RequestBody AppVersion version) {

    System.out.println("🔥 POST RECEIVED");

    return repository.save(version);
}

    // ➜ ПОЛУЧИТЬ ВСЕ ВЕРСИИ
    @GetMapping
    public List<AppVersion> getAll() {
        return repository.findAll();
    }
}