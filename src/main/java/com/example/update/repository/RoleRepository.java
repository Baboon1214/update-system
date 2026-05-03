package com.example.update.repository;

import com.example.update.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

// Репозиторий для работы с сущностью Role
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);  // Метод для поиска роли по имени
}