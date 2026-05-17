package com.example.update.repository;

import com.example.update.model.User;
import com.example.update.model.UserDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, Long> {
    
    Optional<UserDevice> findByUserAndPlatform(User user, String platform);
    
    // ДОБАВЬТЕ ЭТОТ МЕТОД:
    List<UserDevice> findByUser(User user);
    
    @Modifying
    @Transactional
    @Query("UPDATE UserDevice d SET d.currentVersion = :newVersion, d.lastUpdateLog = :lastUpdate WHERE d.user = :user AND d.platform = :platform")
    int updateUserVersion(@Param("user") User user,
    @Param("platform") String platform,
    @Param("newVersion") String newVersion,
    @Param("lastUpdate") LocalDateTime lastUpdate);
}