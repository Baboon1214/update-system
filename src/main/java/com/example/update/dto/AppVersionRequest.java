package com.example.update.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AppVersionRequest {

    @NotBlank(message = "Version is required")
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+$", message = "Version must follow semantic versioning (e.g. 1.0.0)")
    private String version;

    @NotBlank(message = "Platform is required")
    @Pattern(regexp = "^(android|ios|windows|macos|linux)$", message = "Platform must be android, ios, windows, macos, or linux")
    private String platform;

    private String changelog;

    @Pattern(regexp = "^(OPTIONAL|RECOMMENDED|MANDATORY|DEPRECATED)$", message = "UpdateType must be OPTIONAL, RECOMMENDED, MANDATORY, or DEPRECATED")
    private String updateType;

    private boolean active;

    // Геттеры и сеттеры
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }

    public String getChangelog() { return changelog; }
    public void setChangelog(String changelog) { this.changelog = changelog; }

    public String getUpdateType() { return updateType; }
    public void setUpdateType(String updateType) { this.updateType = updateType; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}