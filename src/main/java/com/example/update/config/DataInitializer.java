package com.example.update.config;

import com.example.update.model.Permission;
import com.example.update.model.Role;
import com.example.update.model.User;
import com.example.update.repository.PermissionRepository;
import com.example.update.repository.RoleRepository;
import com.example.update.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DataInitializer(PermissionRepository permissionRepository, 
        RoleRepository roleRepository,
        UserRepository userRepository,
        BCryptPasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing data...");

        // 1. Создаём права (permissions)
        Permission versionRead = getOrCreatePermission("VERSION_READ", "Read versions");
        Permission versionCreate = getOrCreatePermission("VERSION_CREATE", "Create versions");
        Permission versionUpdate = getOrCreatePermission("VERSION_UPDATE", "Update versions");
        Permission versionDelete = getOrCreatePermission("VERSION_DELETE", "Delete versions");
        Permission statsView = getOrCreatePermission("STATS_VIEW", "View statistics");

        // 2. Создаём роль ADMIN
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role("ADMIN");
                    role.setPermissions(Set.of(versionRead, versionCreate, versionUpdate, versionDelete, statsView));
                    log.info("Created ADMIN role");
                    return roleRepository.save(role);
                });

        // 3. Создаём роль USER
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role("USER");
                    role.setPermissions(Set.of(versionRead));
                    log.info("Created USER role");
                    return roleRepository.save(role);
                });

        // 4. Создаём пользователя admin (пароль: 12345678)
        if (userRepository.findByUsername("admin") == null) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("12345678"));
            admin.setEnabled(true);
            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
            log.info("Created admin user (password: 12345678) with ADMIN role");
        } else {
            log.info("Admin user already exists");
        }

        // 5. Создаём обычного пользователя user (пароль: 12345678)
        if (userRepository.findByUsername("user") == null) {
            User regularUser = new User();
            regularUser.setUsername("user");
            regularUser.setPassword(passwordEncoder.encode("12345678"));
            regularUser.setEnabled(true);
            regularUser.setRoles(Set.of(userRole));
            userRepository.save(regularUser);
            log.info("Created regular user (username: user, password: 12345678) with USER role");
        } else {
            log.info("Regular user already exists");
        }

        log.info("Data initialization complete.");
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