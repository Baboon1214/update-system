package com.example.update.dto;

import java.util.Map;

public class UpdateStatsDTO {
    private String version;
    private String updateType;
    private Map<String, Integer> usersCount;
    private Map<String, Double> platformRates;
    private Double globalUpdateRate;  // 👈 ДОБАВЛЕНО

    // Геттеры и сеттеры
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getUpdateType() { return updateType; }
    public void setUpdateType(String updateType) { this.updateType = updateType; }

    public Map<String, Integer> getUsersCount() { return usersCount; }
    public void setUsersCount(Map<String, Integer> usersCount) { this.usersCount = usersCount; }

    public Map<String, Double> getPlatformRates() { return platformRates; }
    public void setPlatformRates(Map<String, Double> platformRates) { this.platformRates = platformRates; }

    public Double getGlobalUpdateRate() { return globalUpdateRate; }
    public void setGlobalUpdateRate(Double globalUpdateRate) { this.globalUpdateRate = globalUpdateRate; }
}