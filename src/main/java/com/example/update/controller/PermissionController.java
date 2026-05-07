package com.example.update.controller;

import com.example.update.model.Permission;
import com.example.update.repository.PermissionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@SecurityRequirement(name = "bearerAuth")
public class PermissionController {

    private static final Logger log = LoggerFactory.getLogger(PermissionController.class);
    private final PermissionRepository permissionRepository;

    public PermissionController(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @GetMapping
    @Operation(summary = "Получить все права доступа")
    public List<Permission> getAllPermissions() {
        log.info("Fetching all permissions");
        return permissionRepository.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить права доступа по ID")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Long id) {
        log.info("Fetching permission id: {}", id);
        return permissionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Создать права доступа")
    public ResponseEntity<Permission> createPermission(@Valid @RequestBody Permission permission) {
        log.info("Creating new permission: {}", permission.getName());
        Permission saved = permissionRepository.save(permission);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить права доступа по ID")
    public ResponseEntity<Permission> updatePermission(@PathVariable Long id, @Valid @RequestBody Permission permission) {
        log.info("Updating permission id: {}", id);
        return permissionRepository.findById(id)
                .map(existing -> {
                    existing.setName(permission.getName());
                    existing.setDescription(permission.getDescription());
                    Permission updated = permissionRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить права доступа по ID")
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        log.warn("Deleting permission id: {}", id);
        if (!permissionRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        permissionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}