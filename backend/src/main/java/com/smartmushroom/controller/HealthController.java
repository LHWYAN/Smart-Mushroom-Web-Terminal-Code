package com.smartmushroom.controller;

import com.smartmushroom.config.AppProperties;
import com.smartmushroom.dto.ApiResponse;
import com.smartmushroom.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "健康检查")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class HealthController {

    private final StatisticsService statisticsService;
    private final AppProperties appProperties;

    @Operation(summary = "健康检查")
    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> stats = statisticsService.getStatistics();
        Map<String, Object> data = new HashMap<>();
        data.put("status", "running");
        data.put("db_type", appProperties.getDbType());
        data.put("data_source", appProperties.getDataSource());
        data.put("total_records", stats.get("total_records"));
        data.put("total_devices", stats.get("total_devices"));
        data.put("total_commands", stats.get("total_commands"));
        return ApiResponse.ok(data);
    }
}
