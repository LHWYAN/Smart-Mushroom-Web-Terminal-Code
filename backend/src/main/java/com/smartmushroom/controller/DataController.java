package com.smartmushroom.controller;

import com.smartmushroom.dto.ApiResponse;
import com.smartmushroom.entity.SensorData;
import com.smartmushroom.service.LatestDataHolder;
import com.smartmushroom.service.SensorDataService;
import com.smartmushroom.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "传感器数据")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class DataController {

    private final SensorDataService sensorDataService;
    private final StatisticsService statisticsService;
    private final LatestDataHolder latestDataHolder;

    @Operation(summary = "获取最新数据")
    @GetMapping("/latest")
    public ApiResponse<SensorData> latest(@RequestParam(required = false) String device_id) {
        SensorData data = latestDataHolder.getLatest();
        if (data == null) {
            data = sensorDataService.queryLatestOne(device_id);
        }
        return ApiResponse.ok(data);
    }

    @Operation(summary = "查询历史数据")
    @GetMapping("/history")
    public ApiResponse<List<SensorData>> history(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(required = false) String device_id,
            @RequestParam(required = false) String start_time,
            @RequestParam(required = false) String end_time) {
        int safeLimit = Math.min(Math.max(limit, 1), 1000);
        List<SensorData> rows = sensorDataService.queryHistory(
                safeLimit, offset, device_id, start_time, end_time);
        return ApiResponse.ok(rows, rows.size());
    }

    @Operation(summary = "手动插入传感器数据")
    @PostMapping("/sensor-data")
    public ApiResponse<Map<String, Long>> insert(
            @RequestParam(defaultValue = "roomone") String device_id,
            @RequestParam(defaultValue = "") String Temp,
            @RequestParam(defaultValue = "") String Humi,
            @RequestParam(defaultValue = "") String Lumi,
            @RequestParam(defaultValue = "OFF") String LampST,
            @RequestParam(defaultValue = "OFF") String CondST,
            @RequestParam(defaultValue = "OFF") String VentST,
            @RequestParam(defaultValue = "OFF") String BuzzerST,
            @RequestParam(defaultValue = "0") String Smoke,
            @RequestParam(defaultValue = "400") String CO2) {
        Map<String, String> data = new HashMap<>();
        data.put("device_id", device_id);
        data.put("Temp", Temp);
        data.put("Humi", Humi);
        data.put("Lumi", Lumi);
        data.put("LampST", LampST);
        data.put("CondST", CondST);
        data.put("VentST", VentST);
        data.put("BuzzerST", BuzzerST);
        data.put("Smoke", Smoke);
        data.put("CO2", CO2);
        Long id = sensorDataService.insert(data);
        return ApiResponse.ok(Map.of("id", id));
    }

    @Operation(summary = "修改传感器数据")
    @PutMapping("/sensor-data/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestParam Map<String, String> params) {
        if (sensorDataService.update(id, params)) {
            return ApiResponse.ok(null);
        }
        return ApiResponse.fail("记录不存在或未做修改");
    }

    @Operation(summary = "删除单条传感器数据")
    @DeleteMapping("/sensor-data/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (sensorDataService.delete(id)) {
            return ApiResponse.ok(null);
        }
        return ApiResponse.fail("记录不存在");
    }

    @Operation(summary = "批量删除传感器数据")
    @DeleteMapping("/sensor-data")
    public ApiResponse<Map<String, Integer>> deleteBatch(
            @RequestParam(required = false) String device_id,
            @RequestParam(required = false) String before_time) {
        int deleted = sensorDataService.deleteBatch(device_id, before_time);
        return ApiResponse.ok(Map.of("deleted", deleted));
    }

    @Operation(summary = "统计数据")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> statistics() {
        return ApiResponse.ok(statisticsService.getStatistics());
    }
}
