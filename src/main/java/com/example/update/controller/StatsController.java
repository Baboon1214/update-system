package com.example.update.controller;

import com.example.update.dto.UpdateStatsDTO;
import com.example.update.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@SecurityRequirement(name = "bearerAuth")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/updates")
    @Operation(summary = "Получить статистику распространения версий")
    public List<UpdateStatsDTO> getUpdateStats() {
        return statsService.getUpdateStats();
    }
}