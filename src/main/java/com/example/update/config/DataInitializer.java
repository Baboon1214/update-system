package com.example.update.config;

import com.example.update.model.Permission;
import com.example.update.model.Role;
import com.example.update.repository.PermissionRepository;
import com.example.update.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public DataInitializer(PermissionRepository permissionRepository, RoleRepository roleRepository) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        // Создание прав (permissions)
        Permission userCreate = getOrCreatePermission("USER_CREATE", "Создание пользователей");
        Permission userRead   = getOrCreatePermission("USER_READ",   "Просмотр пользователей");
        Permission userUpdate = getOrCreatePermission("USER_UPDATE", "Редактирование пользователей");
        Permission userDelete = getOrCreatePermission("USER_DELETE", "Удаление пользователей");

        Permission versionCreate = getOrCreatePermission("VERSION_CREATE", "Создание версий");
        Permission versionRead   = getOrCreatePermission("VERSION_READ",   "Просмотр версий");
        Permission versionUpdate = getOrCreatePermission("VERSION_UPDATE", "Редактирование версий");
        Permission versionDelete = getOrCreatePermission("VERSION_DELETE", "Удаление версий");

        Permission statsView = getOrCreatePermission("STATS_VIEW", "Просмотр статистики");

        // Создание роли ADMIN
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role role = new Role("ADMIN");
                    role.setPermissions(Set.of(userCreate, userRead, userUpdate, userDelete,
                            versionCreate, versionRead, versionUpdate, versionDelete, statsView));
                    return roleRepository.save(role);
                });

        // Создание роли USER (только чтение версий)
        Role userRole = roleRepository.findByName("USER")
                .orElseGet(() -> {
                    Role role = new Role("USER");
                    role.setPermissions(Set.of(versionRead));
                    return roleRepository.save(role);
                });

        System.out.println("✅ Data initialized: ADMIN and USER roles with permissions");
    }

    private Permission getOrCreatePermission(String name, String description) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> permissionRepository.save(new Permission(name, description)));
    }
}