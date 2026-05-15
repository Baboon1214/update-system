package com.example.update.service;

import com.example.update.dto.UpdateStatsDTO;
import com.example.update.model.AppVersion;
import com.example.update.model.UserDevice;
import com.example.update.repository.AppVersionRepository;
import com.example.update.repository.UserDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private static final Logger log = LoggerFactory.getLogger(StatsService.class);
    private final AppVersionRepository appVersionRepository;
    private final UserDeviceRepository userDeviceRepository;

    public StatsService(AppVersionRepository appVersionRepository, 
                        UserDeviceRepository userDeviceRepository) {
        this.appVersionRepository = appVersionRepository;
        this.userDeviceRepository = userDeviceRepository;
    }

    public List<UpdateStatsDTO> getUpdateStats() {
        log.debug("Calculating update statistics");
        List<AppVersion> versions = appVersionRepository.findAll();
        List<UserDevice> devices = userDeviceRepository.findAll();

        // 👇 ИЗМЕНЕНО: считаем уникальных пользователей по user.id
        long totalUsers = devices.stream()
                .map(device -> device.getUser().getId())
                .distinct()
                .count();
        
        log.info("Total distinct users: {}", totalUsers);

        return versions.stream().map(version -> {
            UpdateStatsDTO dto = new UpdateStatsDTO();
            dto.setVersion(version.getVersion());

            // 👇 ИЗМЕНЕНО: группируем по платформам, считаем устройства
            Map<String, Integer> usersCount = new HashMap<>();
            for (UserDevice device : devices) {
                if (device.getCurrentVersion().equals(version.getVersion())) {
                    String platform = device.getPlatform();
                    usersCount.put(platform, usersCount.getOrDefault(platform, 0) + 1);
                }
            }
            dto.setUsersCount(usersCount);

            // 👇 ИЗМЕНЕНО: считаем уникальных пользователей на этой версии
            long versionUsers = devices.stream()
                    .filter(d -> d.getCurrentVersion().equals(version.getVersion()))
                    .map(d -> d.getUser().getId())
                    .distinct()
                    .count();
                    
            dto.setGlobalUpdateRate(totalUsers > 0 ? (versionUsers * 100.0 / totalUsers) : 0.0);

            log.debug("Version {}: {} users, {:.2f}%", version.getVersion(), versionUsers, dto.getGlobalUpdateRate());
            return dto;
        }).collect(Collectors.toList());
    }
}