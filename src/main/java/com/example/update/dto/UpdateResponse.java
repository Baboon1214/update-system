package com.example.update.dto;

public class UpdateResponse {
    private boolean updateAvailable;
    private String latestVersion;
    private String updateType;
    private String changelog;
    private boolean forceUpdate;

    public boolean isUpdateAvailable() { return updateAvailable; }
    public void setUpdateAvailable(boolean updateAvailable) { this.updateAvailable = updateAvailable; }
    public String getLatestVersion() { return latestVersion; }
    public void setLatestVersion(String latestVersion) { this.latestVersion = latestVersion; }
    public String getUpdateType() { return updateType; }
    public void setUpdateType(String updateType) { this.updateType = updateType; }
    public String getChangelog() { return changelog; }
    public void setChangelog(String changelog) { this.changelog = changelog; }
    public boolean isForceUpdate() { return forceUpdate; }
    public void setForceUpdate(boolean forceUpdate) { this.forceUpdate = forceUpdate; }
}