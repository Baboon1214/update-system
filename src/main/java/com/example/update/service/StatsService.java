package com.example.update.service;

import com.example.update.dto.UpdateStatsDTO;
import com.example.update.model.UserDevice;
import com.example.update.repository.UserDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatsService {

    private static final Logger log = LoggerFactory.getLogger(StatsService.class);
    private final UserDeviceRepository userDeviceRepository;

    public StatsService(UserDeviceRepository userDeviceRepository) {
        this.userDeviceRepository = userDeviceRepository;
    }

    public List<UpdateStatsDTO> getUpdateStats() {
        List<UserDevice> allDevices = userDeviceRepository.findAll();
        
        if (allDevices.isEmpty()) {
            log.info("No devices found");
            return new ArrayList<>();
        }
        
        Map<String, Map<String, Set<Long>>> statsMap = new LinkedHashMap<>();
        
        for (UserDevice device : allDevices) {
            String platform = device.getPlatform();
            String version = device.getCurrentVersion();
            Long userId = device.getUser().getId();
            
            statsMap.putIfAbsent(platform, new LinkedHashMap<>());
            statsMap.get(platform).putIfAbsent(version, new HashSet<>());
            statsMap.get(platform).get(version).add(userId);
        }
        
        Map<String, Long> totalUsersByPlatform = new HashMap<>();
        for (Map.Entry<String, Map<String, Set<Long>>> platformEntry : statsMap.entrySet()) {
            String platform = platformEntry.getKey();
            Set<Long> allUsersOnPlatform = new HashSet<>();
            for (Set<Long> users : platformEntry.getValue().values()) {
                allUsersOnPlatform.addAll(users);
            }
            totalUsersByPlatform.put(platform, (long) allUsersOnPlatform.size());
        }
        
        List<UpdateStatsDTO> result = new ArrayList<>();
        for (Map.Entry<String, Map<String, Set<Long>>> platformEntry : statsMap.entrySet()) {
            String platform = platformEntry.getKey();
            long totalUsersOnPlatform = totalUsersByPlatform.get(platform);
            
            for (Map.Entry<String, Set<Long>> versionEntry : platformEntry.getValue().entrySet()) {
                String version = versionEntry.getKey();
                int usersCount = versionEntry.getValue().size();
                double updateRate = totalUsersOnPlatform > 0 
                    ? (usersCount * 100.0 / totalUsersOnPlatform) 
                    : 0;
                
                Map<String, Integer> usersCountMap = new HashMap<>();
                usersCountMap.put(platform, usersCount);
                result.add(new UpdateStatsDTO(version, usersCountMap, updateRate));
            }
        }
        
        result.sort((a, b) -> {
            String platformA = a.getUsersCount().keySet().iterator().next();
            String platformB = b.getUsersCount().keySet().iterator().next();
            
            int platformCompare = platformA.compareTo(platformB);
            if (platformCompare != 0) return platformCompare;
            
            return b.getVersion().compareTo(a.getVersion());
        });
        
        log.info("Generated stats: {} entries", result.size());
        return result;
    }
}