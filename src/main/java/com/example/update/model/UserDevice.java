package com.example.update.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_devices")
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String platform;
    private String currentVersion;
    private LocalDateTime lastSeen;
    private LocalDateTime lastUpdateLog;

    public UserDevice() {}
    public UserDevice(String userId, String platform, String currentVersion) {
        this.userId = userId;
        this.platform = platform;
        this.currentVersion = currentVersion;
        this.lastSeen = LocalDateTime.now();
    }

    // геттеры/сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(String currentVersion) { this.currentVersion = currentVersion; }
    public LocalDateTime getLastSeen() { return lastSeen; }
    public void setLastSeen(LocalDateTime lastSeen) { this.lastSeen = lastSeen; }
    public LocalDateTime getLastUpdateLog() { return lastUpdateLog; }
    public void setLastUpdateLog(LocalDateTime lastUpdateLog) { this.lastUpdateLog = lastUpdateLog; }
}