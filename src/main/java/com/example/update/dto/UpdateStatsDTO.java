package com.example.update.dto;

import java.util.Map;

public class UpdateStatsDTO {
    private String version;
    private Map<String, Integer> usersCount;
    private Double globalUpdateRate;

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public Map<String, Integer> getUsersCount() { return usersCount; }
    public void setUsersCount(Map<String, Integer> usersCount) { this.usersCount = usersCount; }

    public Double getGlobalUpdateRate() { return globalUpdateRate; }
    public void setGlobalUpdateRate(Double globalUpdateRate) { this.globalUpdateRate = globalUpdateRate; }
}