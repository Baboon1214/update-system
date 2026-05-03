package com.example.update.repository;

import com.example.update.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// Репозиторий для работы с сущностью User
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);  // Метод для поиска пользователя по имени
}