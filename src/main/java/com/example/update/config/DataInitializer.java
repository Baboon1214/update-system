package com.example.update.config;

import com.example.update.model.Permission;
import com.example.update.model.Role;
import com.example.update.repository.PermissionRepository;
import com.example.update.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public DataInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing data...");

        Permission versionRead = getOrCreatePermission("VERSION_READ", "Read versions");
        Permission versionCreate = getOrCreatePermission("VERSION_CREATE", "Create versions");
        Permission versionUpdate = getOrCreatePermission("VERSION_UPDATE", "Update versions");
        Permission versionDelete = getOrCreatePermission("VERSION_DELETE", "Delete versions");
        Permission statsView = getOrCreatePermission("STATS_VIEW", "View statistics");

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role("ADMIN");
                    role.setPermissions(Set.of(versionRead, versionCreate, versionUpdate, versionDelete, statsView));
                    log.info("Created ADMIN role");
                    return roleRepository.save(role);
                });

        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role("USER");
                    role.setPermissions(Set.of(versionRead));
                    log.info("Created USER role");
                    return roleRepository.save(role);
                });

        log.info("Data initialization complete. Roles: ADMIN, USER");
    }

    private Permission getOrCreatePermission(String name, String description) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    Permission p = new Permission(name, description);
                    log.debug("Created permission: {}", name);
                    return permissionRepository.save(p);
                });
    }
}