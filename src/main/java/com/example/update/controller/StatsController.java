package com.example.update.controller;

import com.example.update.dto.UpdateStatsDTO;
import com.example.update.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@SecurityRequirement(name = "bearerAuth")
public class StatsController {

    private static final Logger log = LoggerFactory.getLogger(StatsController.class);
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/updates")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить статистику распространения версий (только для ADMIN)")
    public List<UpdateStatsDTO> getUpdateStats() {
        log.info("Fetching update statistics");
        return statsService.getUpdateStats();
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Экспорт статистики в CSV (только для ADMIN)")
    public ResponseEntity<String> exportCsv() {
        log.info("Exporting statistics to CSV");
        List<UpdateStatsDTO> stats = statsService.getUpdateStats();

        StringBuilder csv = new StringBuilder();
        
        csv.append("Version;UpdateType;Platform;UsersCount;UpdateRate(%)\n");
        
        for (UpdateStatsDTO dto : stats) {
            String version = dto.getVersion();
            String updateType = dto.getUpdateType() != null ? dto.getUpdateType() : "UNKNOWN";
            
            Map<String, Integer> usersCount = dto.getUsersCount();
            Map<String, Double> platformRates = dto.getPlatformRates();
            
            for (Map.Entry<String, Integer> entry : usersCount.entrySet()) {
                String platform = entry.getKey();
                int count = entry.getValue();
                
                // 👇 БЕРЁМ ПРОЦЕНТ ИМЕННО ДЛЯ ЭТОЙ ПЛАТФОРМЫ
                double rate = platformRates != null ? platformRates.getOrDefault(platform, 0.0) : 0.0;
                
                csv.append(String.format("%s;%s;%s;%d;%.2f\n", 
                    version, updateType, platform, count, rate));
            }
        }

        log.info("CSV export completed");
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=update_stats.csv")
                .header("Content-Type", "text/csv; charset=windows-1251")
                .body(csv.toString());
    }
}