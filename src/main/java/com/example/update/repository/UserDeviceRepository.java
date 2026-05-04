package com.example.update.repository;

import com.example.update.model.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {

    Optional<UserDevice> findByUserIdAndPlatform(String userId, String platform);

    List<UserDevice> findAll();

    long countByPlatform(String platform);

    long countByPlatformAndCurrentVersion(String platform, String version);

    @Modifying
    @Transactional
    @Query("UPDATE UserDevice d SET d.currentVersion = :newVersion, d.lastUpdateLog = :now WHERE d.userId = :userId AND d.platform = :platform")
    int updateUserVersion(String userId, String platform, String newVersion, LocalDateTime now);
}