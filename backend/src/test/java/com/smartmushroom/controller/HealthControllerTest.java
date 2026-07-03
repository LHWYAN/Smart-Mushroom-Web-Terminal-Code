package com.smartmushroom.controller;

import com.smartmushroom.config.AppProperties;
import com.smartmushroom.mapper.DeviceCommandMapper;
import com.smartmushroom.mapper.DeviceInfoMapper;
import com.smartmushroom.mapper.SensorDataMapper;
import com.smartmushroom.service.StatisticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link HealthController} 切片测试。
 * 验证 /health 端点返回系统状态聚合信息。
 */
@DisplayName("HealthController - 健康检查接口")
@WebMvcTest(HealthController.class)
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;
    @MockBean
    private AppProperties appProperties;
    @MockBean
    private DeviceInfoMapper deviceInfoMapper;
    @MockBean
    private SensorDataMapper sensorDataMapper;
    @MockBean
    private DeviceCommandMapper deviceCommandMapper;

    @Test
    @DisplayName("GET /health 返回系统状态聚合信息")
    void health_returnsSystemStatus() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_records", 100L);
        stats.put("total_devices", 5L);
        stats.put("total_commands", 20L);
        when(statisticsService.getStatistics()).thenReturn(stats);
        when(appProperties.getDbType()).thenReturn("sqlite");
        when(appProperties.getDataSource()).thenReturn("simulate");

        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.status").value("running"))
                .andExpect(jsonPath("$.data.db_type").value("sqlite"))
                .andExpect(jsonPath("$.data.data_source").value("simulate"))
                .andExpect(jsonPath("$.data.total_records").value(100))
                .andExpect(jsonPath("$.data.total_devices").value(5))
                .andExpect(jsonPath("$.data.total_commands").value(20));
    }

    @Test
    @DisplayName("GET /health 始终返回 status=running")
    void health_alwaysReturnsRunningStatus() throws Exception {
        when(statisticsService.getStatistics()).thenReturn(new HashMap<>());
        when(appProperties.getDbType()).thenReturn("kingbase");
        when(appProperties.getDataSource()).thenReturn("amqp");

        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("running"))
                .andExpect(jsonPath("$.data.db_type").value("kingbase"));
    }
}
