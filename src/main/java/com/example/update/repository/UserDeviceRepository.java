package com.example.update.repository;

import com.example.update.model.User;
import com.example.update.model.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    // Ищем по пользователю и платформе
    Optional<UserDevice> findByUserAndPlatform(User user, String platform);

    // Обновление версии
    @Modifying
    @Transactional
    @Query("UPDATE UserDevice d SET d.currentVersion = :newVersion, d.lastUpdateLog = :now WHERE d.user = :user AND d.platform = :platform")
    int updateUserVersion(User user, String platform, String newVersion, LocalDateTime now);
}