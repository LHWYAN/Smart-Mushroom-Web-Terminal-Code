package com.smartmushroom.service;

import com.smartmushroom.entity.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final SensorDataService sensorDataService;
    private final DeviceService deviceService;
    private final CommandService commandService;
    private final LatestDataHolder latestDataHolder;

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_records", sensorDataService.count());
        stats.put("total_devices", deviceService.count());
        stats.put("total_commands", commandService.count());

        SensorData latest = latestDataHolder.getLatest();
        if (latest == null) {
            latest = sensorDataService.queryLatestOne(null);
        }
        stats.put("latest", latest);
        return stats;
    }
}
