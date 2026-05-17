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

        // Группируем устройства по платформам
        Map<String, Long> totalDevicesByPlatform = devices.stream()
                .filter(d -> d.getPlatform() != null && !d.getPlatform().isEmpty())
                .collect(Collectors.groupingBy(
                    UserDevice::getPlatform,
                    Collectors.counting()
                ));

        log.info("Total devices by platform: {}", totalDevicesByPlatform);

        // Группируем устройства по версиям и платформам
        Map<String, Map<String, Long>> devicesByVersionAndPlatform = devices.stream()
                .filter(d -> d.getCurrentVersion() != null && d.getPlatform() != null)
                .collect(Collectors.groupingBy(
                    UserDevice::getCurrentVersion,
                    Collectors.groupingBy(
                        UserDevice::getPlatform,
                        Collectors.counting()
                    )
                ));

        return versions.stream()
                .filter(version -> version.getVersion() != null)
                .map(version -> {
                    UpdateStatsDTO dto = new UpdateStatsDTO();
                    dto.setVersion(version.getVersion());
                    dto.setUpdateType(version.getUpdateType());

                    Map<String, Integer> usersCount = new HashMap<>();
                    Map<String, Double> platformRates = new HashMap<>();

                    Map<String, Long> platformCounts = devicesByVersionAndPlatform
                            .getOrDefault(version.getVersion(), new HashMap<>());

                    for (Map.Entry<String, Long> entry : platformCounts.entrySet()) {
                        String platform = entry.getKey();
                        Long count = entry.getValue();
                        usersCount.put(platform, count.intValue());

                        Long totalOnPlatform = totalDevicesByPlatform.getOrDefault(platform, 1L);
                        double rate = (count * 100.0) / totalOnPlatform;
                        platformRates.put(platform, rate);
                    }

                    dto.setUsersCount(usersCount);
                    dto.setPlatformRates(platformRates);
                    // Для общего процента берём средний по платформам (или можно оставить 0)
                    double avgRate = platformRates.values().stream()
                            .mapToDouble(Double::doubleValue)
                            .average()
                            .orElse(0.0);
                    dto.setGlobalUpdateRate(avgRate);

                    log.debug("Version {}: devices by platform: {}, rates: {}", 
                        version.getVersion(), usersCount, platformRates);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}