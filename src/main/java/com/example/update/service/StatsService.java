package com.example.update.service;

import com.example.update.dto.UpdateStatsDTO;
import com.example.update.model.AppVersion;
import com.example.update.model.UserDevice;
import com.example.update.repository.AppVersionRepository;
import com.example.update.repository.UserDeviceRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final AppVersionRepository appVersionRepository;
    private final UserDeviceRepository userDeviceRepository;

    public StatsService(AppVersionRepository appVersionRepository, 
                        UserDeviceRepository userDeviceRepository) {
        this.appVersionRepository = appVersionRepository;
        this.userDeviceRepository = userDeviceRepository;
    }

    public List<UpdateStatsDTO> getUpdateStats() {
        List<AppVersion> versions = appVersionRepository.findAll();
        List<UserDevice> devices = userDeviceRepository.findAll();
        
        long totalUsers = devices.stream()
                .map(UserDevice::getUserId)
                .distinct()
                .count();
        
        return versions.stream().map(version -> {
            UpdateStatsDTO dto = new UpdateStatsDTO();
            dto.setVersion(version.getVersion());
            
            Map<String, Integer> usersCount = new HashMap<>();
            for (UserDevice device : devices) {
                if (device.getCurrentVersion().equals(version.getVersion())) {
                    String platform = device.getPlatform();
                    usersCount.put(platform, usersCount.getOrDefault(platform, 0) + 1);
                }
            }
            dto.setUsersCount(usersCount);
            
            long versionUsers = devices.stream()
                    .filter(d -> d.getCurrentVersion().equals(version.getVersion()))
                    .count();
            dto.setGlobalUpdateRate(totalUsers > 0 ? (versionUsers * 100.0 / totalUsers) : 0.0);
            
            return dto;
        }).collect(Collectors.toList());
    }
}