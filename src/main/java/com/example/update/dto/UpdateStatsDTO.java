package com.example.update.dto;

import java.util.Map;

public class UpdateStatsDTO {
    private String version;
    private Map<String, Integer> usersCount;
    private double globalUpdateRate;

    public UpdateStatsDTO(String version, Map<String, Integer> usersCount, double globalUpdateRate) {
        this.version = version;
        this.usersCount = usersCount;
        this.globalUpdateRate = globalUpdateRate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Integer> getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(Map<String, Integer> usersCount) {
        this.usersCount = usersCount;
    }

    public double getGlobalUpdateRate() {
        return globalUpdateRate;
    }

    public void setGlobalUpdateRate(double globalUpdateRate) {
        this.globalUpdateRate = globalUpdateRate;
    }
}