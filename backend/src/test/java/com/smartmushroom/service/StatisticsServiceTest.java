package com.smartmushroom.service;

import com.smartmushroom.entity.SensorData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * {@link StatisticsService} 单元测试。
 * 验证缓存优先策略：有 LatestDataHolder 缓存时优先使用，无缓存时回退数据库查询。
 */
@DisplayName("StatisticsService - 统计聚合")
@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private SensorDataService sensorDataService;
    @Mock
    private DeviceService deviceService;
    @Mock
    private CommandService commandService;
    @Mock
    private LatestDataHolder latestDataHolder;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    @DisplayName("getStatistics() 有 LatestDataHolder 缓存时优先使用缓存数据")
    void getStatistics_withCache_usesCachedData() {
        SensorData cached = new SensorData();
        cached.setDeviceId("dev-001");
        cached.setTemp("25.0");
        when(latestDataHolder.getLatest()).thenReturn(cached);
        when(sensorDataService.count()).thenReturn(100L);
        when(deviceService.count()).thenReturn(5L);
        when(commandService.count()).thenReturn(20L);

        Map<String, Object> stats = statisticsService.getStatistics();

        assertEquals(100L, stats.get("total_records"));
        assertEquals(5L, stats.get("total_devices"));
        assertEquals(20L, stats.get("total_commands"));
        assertSame(cached, stats.get("latest"), "应使用缓存数据而非查询数据库");
        verify(sensorDataService, never()).queryLatestOne(any());
    }

    @Test
    @DisplayName("getStatistics() 无缓存时回退数据库查询 queryLatestOne(null)")
    void getStatistics_withoutCache_fallsBackToDb() {
        SensorData dbData = new SensorData();
        dbData.setDeviceId("dev-002");
        dbData.setTemp("22.0");
        when(latestDataHolder.getLatest()).thenReturn(null);
        when(sensorDataService.queryLatestOne(null)).thenReturn(dbData);
        when(sensorDataService.count()).thenReturn(50L);
        when(deviceService.count()).thenReturn(3L);
        when(commandService.count()).thenReturn(10L);

        Map<String, Object> stats = statisticsService.getStatistics();

        assertEquals(50L, stats.get("total_records"));
        assertEquals(3L, stats.get("total_devices"));
        assertEquals(10L, stats.get("total_commands"));
        assertSame(dbData, stats.get("latest"), "无缓存时应回退到数据库查询");
        verify(sensorDataService).queryLatestOne(null);
    }

    @Test
    @DisplayName("getStatistics() 缓存和数据库都无数据时 latest 为 null")
    void getStatistics_noDataAnywhere_latestIsNull() {
        when(latestDataHolder.getLatest()).thenReturn(null);
        when(sensorDataService.queryLatestOne(null)).thenReturn(null);
        when(sensorDataService.count()).thenReturn(0L);
        when(deviceService.count()).thenReturn(0L);
        when(commandService.count()).thenReturn(0L);

        Map<String, Object> stats = statisticsService.getStatistics();

        assertEquals(0L, stats.get("total_records"));
        assertEquals(0L, stats.get("total_devices"));
        assertEquals(0L, stats.get("total_commands"));
        assertNull(stats.get("latest"));
    }

    @Test
    @DisplayName("getStatistics() 返回的 Map 包含全部 4 个统计字段")
    void getStatistics_returnsAllFields() {
        when(latestDataHolder.getLatest()).thenReturn(new SensorData());
        when(sensorDataService.count()).thenReturn(1L);
        when(deviceService.count()).thenReturn(1L);
        when(commandService.count()).thenReturn(1L);

        Map<String, Object> stats = statisticsService.getStatistics();

        assertTrue(stats.containsKey("total_records"));
        assertTrue(stats.containsKey("total_devices"));
        assertTrue(stats.containsKey("total_commands"));
        assertTrue(stats.containsKey("latest"));
    }
}
