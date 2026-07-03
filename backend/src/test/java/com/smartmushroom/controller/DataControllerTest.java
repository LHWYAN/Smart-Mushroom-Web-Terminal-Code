package com.smartmushroom.controller;

import com.smartmushroom.entity.SensorData;
import com.smartmushroom.mapper.DeviceCommandMapper;
import com.smartmushroom.mapper.DeviceInfoMapper;
import com.smartmushroom.mapper.SensorDataMapper;
import com.smartmushroom.service.LatestDataHolder;
import com.smartmushroom.service.SensorDataService;
import com.smartmushroom.service.StatisticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link DataController} 切片测试。
 * 重点验证 limit 钳制逻辑、缓存回退、CRUD 响应格式。
 */
@DisplayName("DataController - 传感器数据接口")
@WebMvcTest(DataController.class)
class DataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SensorDataService sensorDataService;
    @MockBean
    private StatisticsService statisticsService;
    @MockBean
    private LatestDataHolder latestDataHolder;
    @MockBean
    private DeviceInfoMapper deviceInfoMapper;
    @MockBean
    private SensorDataMapper sensorDataMapper;
    @MockBean
    private DeviceCommandMapper deviceCommandMapper;

    // ==================== GET /latest ====================

    @Test
    @DisplayName("GET /latest 有缓存数据时返回缓存")
    void latest_withCache_returnsCachedData() throws Exception {
        SensorData data = new SensorData();
        data.setDeviceId("dev-001");
        data.setTemp("25.0");
        when(latestDataHolder.getLatest()).thenReturn(data);

        mockMvc.perform(get("/api/v1/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.device_id").value("dev-001"))
                .andExpect(jsonPath("$.data.Temp").value("25.0"));

        verify(sensorDataService, never()).queryLatestOne(any());
    }

    @Test
    @DisplayName("GET /latest 无缓存时回退数据库查询")
    void latest_withoutCache_fallsBackToDb() throws Exception {
        SensorData data = new SensorData();
        data.setDeviceId("dev-002");
        when(latestDataHolder.getLatest()).thenReturn(null);
        when(sensorDataService.queryLatestOne(any())).thenReturn(data);

        mockMvc.perform(get("/api/v1/latest").param("device_id", "dev-002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.device_id").value("dev-002"));

        verify(sensorDataService).queryLatestOne("dev-002");
    }

    // ==================== GET /history ====================

    @Test
    @DisplayName("GET /history limit=100 正常查询")
    void history_normalLimit_returnsData() throws Exception {
        SensorData d1 = new SensorData();
        d1.setTemp("20.0");
        when(sensorDataService.queryHistory(eq(100), eq(0), any(), any(), any()))
                .thenReturn(List.of(d1));

        mockMvc.perform(get("/api/v1/history").param("limit", "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].Temp").value("20.0"))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @DisplayName("GET /history limit=0 钳制为 1")
    void history_limitZero_clampedToOne() throws Exception {
        when(sensorDataService.queryHistory(eq(1), eq(0), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/history").param("limit", "0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(sensorDataService).queryHistory(eq(1), eq(0), any(), any(), any());
    }

    @Test
    @DisplayName("GET /history limit=9999 钳制为 1000")
    void history_limitExceedsMax_clampedToThousand() throws Exception {
        when(sensorDataService.queryHistory(eq(1000), eq(0), any(), any(), any()))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/history").param("limit", "9999"))
                .andExpect(status().isOk());

        verify(sensorDataService).queryHistory(eq(1000), eq(0), any(), any(), any());
    }

    // ==================== POST /sensor-data ====================

    @Test
    @DisplayName("POST /sensor-data 完整参数插入成功")
    void insert_withFullParams_returnsId() throws Exception {
        when(sensorDataService.insert(anyMap())).thenReturn(42L);

        mockMvc.perform(post("/api/v1/sensor-data")
                        .param("device_id", "dev-001")
                        .param("Temp", "25.5")
                        .param("CO2", "450"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(42));
    }

    @Test
    @DisplayName("POST /sensor-data 缺省参数使用默认值")
    void insert_withDefaultParams_returnsId() throws Exception {
        when(sensorDataService.insert(anyMap())).thenReturn(1L);

        mockMvc.perform(post("/api/v1/sensor-data"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    // ==================== PUT /sensor-data/{id} ====================

    @Test
    @DisplayName("PUT /sensor-data/{id} 更新成功返回 code=0")
    void update_success_returnsOk() throws Exception {
        when(sensorDataService.update(eq(1L), anyMap())).thenReturn(true);

        mockMvc.perform(put("/api/v1/sensor-data/1").param("Temp", "30.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("PUT /sensor-data/{id} 记录不存在返回 code=-1")
    void update_notFound_returnsFail() throws Exception {
        when(sensorDataService.update(eq(999L), anyMap())).thenReturn(false);

        mockMvc.perform(put("/api/v1/sensor-data/999").param("Temp", "30.0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("记录不存在或未做修改"));
    }

    // ==================== DELETE /sensor-data/{id} ====================

    @Test
    @DisplayName("DELETE /sensor-data/{id} 删除成功返回 code=0")
    void delete_success_returnsOk() throws Exception {
        when(sensorDataService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/sensor-data/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("DELETE /sensor-data/{id} 记录不存在返回 code=-1")
    void delete_notFound_returnsFail() throws Exception {
        when(sensorDataService.delete(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/sensor-data/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("记录不存在"));
    }

    // ==================== DELETE /sensor-data (batch) ====================

    @Test
    @DisplayName("DELETE /sensor-data 批量删除返回删除数量")
    void deleteBatch_returnsDeletedCount() throws Exception {
        when(sensorDataService.deleteBatch(any(), any())).thenReturn(5);

        mockMvc.perform(delete("/api/v1/sensor-data")
                        .param("device_id", "dev-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.deleted").value(5));
    }

    // ==================== GET /statistics ====================

    @Test
    @DisplayName("GET /statistics 返回统计数据")
    void statistics_returnsStats() throws Exception {
        Map<String, Object> stats = Map.of(
                "total_records", 100L,
                "total_devices", 5L,
                "total_commands", 20L
        );
        when(statisticsService.getStatistics()).thenReturn(stats);

        mockMvc.perform(get("/api/v1/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.total_records").value(100));
    }
}
