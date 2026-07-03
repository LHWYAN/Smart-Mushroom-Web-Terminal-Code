package com.smartmushroom.service;

import com.smartmushroom.entity.SensorData;
import com.smartmushroom.mapper.SensorDataMapper;
import com.smartmushroom.test.MybatisPlusTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link SensorDataService} 单元测试。
 * 使用 Mockito Mock SensorDataMapper，验证默认值填充、CRUD、批量删除、查询逻辑。
 */
@DisplayName("SensorDataService - 传感器数据管理")
@ExtendWith(MockitoExtension.class)
class SensorDataServiceTest {

    @BeforeAll
    static void initMybatisPlus() {
        MybatisPlusTestSupport.initTableInfo(SensorData.class);
    }

    @Mock
    private SensorDataMapper sensorDataMapper;

    @InjectMocks
    private SensorDataService sensorDataService;

    // ==================== insert() ====================

    @Test
    @DisplayName("insert() 完整参数插入，返回自增 ID")
    void insert_withFullData_insertsAndReturnsId() {
        SensorData captured = new SensorData();
        captured.setId(99L);
        when(sensorDataMapper.insert(any(SensorData.class))).thenAnswer(invocation -> {
            SensorData row = invocation.getArgument(0);
            row.setId(99L);
            return 1;
        });

        Map<String, String> data = Map.of(
                "device_id", "dev-001",
                "Temp", "25.5",
                "Humi", "80",
                "Lumi", "500",
                "LampST", "ON",
                "CondST", "OFF",
                "VentST", "ON",
                "BuzzerST", "OFF",
                "Smoke", "10",
                "CO2", "450"
        );

        Long id = sensorDataService.insert(data);

        assertEquals(99L, id);
        ArgumentCaptor<SensorData> captor = ArgumentCaptor.forClass(SensorData.class);
        verify(sensorDataMapper).insert(captor.capture());
        SensorData inserted = captor.getValue();
        assertEquals("dev-001", inserted.getDeviceId());
        assertEquals("25.5", inserted.getTemp());
        assertEquals("ON", inserted.getLampST());
        assertEquals("450", inserted.getCo2());
    }

    @Test
    @DisplayName("insert() 缺省参数使用默认值（device_id=roomone, LampST=OFF, CO2=400 等）")
    void insert_withEmptyMap_usesDefaults() {
        when(sensorDataMapper.insert(any(SensorData.class))).thenReturn(1);

        Long id = sensorDataService.insert(Map.of());

        ArgumentCaptor<SensorData> captor = ArgumentCaptor.forClass(SensorData.class);
        verify(sensorDataMapper).insert(captor.capture());
        SensorData inserted = captor.getValue();
        assertEquals("roomone", inserted.getDeviceId());
        assertEquals("", inserted.getTemp());
        assertEquals("OFF", inserted.getLampST());
        assertEquals("OFF", inserted.getCondST());
        assertEquals("OFF", inserted.getVentST());
        assertEquals("OFF", inserted.getBuzzerST());
        assertEquals("0", inserted.getSmoke());
        assertEquals("400", inserted.getCo2());
        assertNotNull(inserted.getCreateTime());
    }

    // ==================== queryLatestOne() ====================

    @Test
    @DisplayName("queryLatestOne() 带 deviceId 过滤，返回最新记录")
    void queryLatestOne_withDeviceId_returnsLatest() {
        SensorData data = new SensorData();
        data.setDeviceId("dev-001");
        data.setTemp("22.0");
        when(sensorDataMapper.selectOne(any())).thenReturn(data);

        SensorData result = sensorDataService.queryLatestOne("dev-001");

        assertNotNull(result);
        assertEquals("dev-001", result.getDeviceId());
        assertEquals("22.0", result.getTemp());
    }

    @Test
    @DisplayName("queryLatestOne() 不带 deviceId（null）查询全部最新")
    void queryLatestOne_withoutDeviceId_queriesAll() {
        SensorData data = new SensorData();
        data.setTemp("23.0");
        when(sensorDataMapper.selectOne(any())).thenReturn(data);

        SensorData result = sensorDataService.queryLatestOne(null);

        assertNotNull(result);
        assertEquals("23.0", result.getTemp());
    }

    @Test
    @DisplayName("queryLatestOne() 空白 deviceId 等同于不过滤")
    void queryLatestOne_withBlankDeviceId_queriesAll() {
        when(sensorDataMapper.selectOne(any())).thenReturn(null);

        SensorData result = sensorDataService.queryLatestOne("  ");

        assertNull(result);
    }

    @Test
    @DisplayName("queryLatestOne() 无数据时返回 null")
    void queryLatestOne_noData_returnsNull() {
        when(sensorDataMapper.selectOne(any())).thenReturn(null);

        assertNull(sensorDataService.queryLatestOne("dev-001"));
    }

    // ==================== queryHistory() ====================

    @Test
    @DisplayName("queryHistory() 带完整参数查询")
    void queryHistory_withAllParams_returnsList() {
        SensorData d1 = new SensorData();
        d1.setTemp("20.0");
        when(sensorDataMapper.selectList(any())).thenReturn(List.of(d1));

        List<SensorData> result = sensorDataService.queryHistory(100, 0, "dev-001", "2024-01-01", "2024-12-31");

        assertEquals(1, result.size());
        assertEquals("20.0", result.get(0).getTemp());
    }

    @Test
    @DisplayName("queryHistory() deviceId/startTime/endTime 为 null 时不加过滤条件")
    void queryHistory_withNullParams_returnsAll() {
        when(sensorDataMapper.selectList(any())).thenReturn(List.of());

        List<SensorData> result = sensorDataService.queryHistory(50, 0, null, null, null);

        assertTrue(result.isEmpty());
    }

    // ==================== update() ====================

    @Test
    @DisplayName("update() 有效字段更新返回 true")
    void update_withValidFields_returnsTrue() {
        when(sensorDataMapper.update(eq(null), any())).thenReturn(1);

        boolean result = sensorDataService.update(1L, Map.of("Temp", "30.0", "CO2", "500"));

        assertTrue(result);
    }

    @Test
    @DisplayName("update() 无有效字段返回 false")
    void update_withNoValidFields_returnsFalse() {
        boolean result = sensorDataService.update(1L, Map.of("unknown", "value"));

        assertFalse(result);
        verify(sensorDataMapper, never()).update(any(), any());
    }

    @Test
    @DisplayName("update() 字段值为 null 时跳过该字段")
    void update_withNullFieldValue_skipsField() {
        when(sensorDataMapper.update(eq(null), any())).thenReturn(1);

        Map<String, String> fields = new HashMap<>();
        fields.put("Temp", null);
        fields.put("CO2", "500");
        boolean result = sensorDataService.update(1L, fields);

        assertTrue(result);
    }

    @Test
    @DisplayName("update() 记录不存在（0 行受影响）返回 false")
    void update_recordNotFound_returnsFalse() {
        when(sensorDataMapper.update(eq(null), any())).thenReturn(0);

        boolean result = sensorDataService.update(999L, Map.of("Temp", "30.0"));

        assertFalse(result);
    }

    // ==================== delete() ====================

    @Test
    @DisplayName("delete() 删除成功返回 true")
    void delete_success_returnsTrue() {
        when(sensorDataMapper.deleteById(1L)).thenReturn(1);

        assertTrue(sensorDataService.delete(1L));
    }

    @Test
    @DisplayName("delete() 记录不存在返回 false")
    void delete_notFound_returnsFalse() {
        when(sensorDataMapper.deleteById(999L)).thenReturn(0);

        assertFalse(sensorDataService.delete(999L));
    }

    // ==================== deleteBatch() ====================

    @Test
    @DisplayName("deleteBatch() 同时按 deviceId 和 beforeTime 条件删除")
    void deleteBatch_withBothConditions_deletesRecords() {
        when(sensorDataMapper.delete(any())).thenReturn(5);

        int deleted = sensorDataService.deleteBatch("dev-001", "2024-01-01 00:00:00");

        assertEquals(5, deleted);
    }

    @Test
    @DisplayName("deleteBatch() 无条件删除全部")
    void deleteBatch_withNullConditions_deletesAll() {
        when(sensorDataMapper.delete(any())).thenReturn(100);

        int deleted = sensorDataService.deleteBatch(null, null);

        assertEquals(100, deleted);
    }

    @Test
    @DisplayName("deleteBatch() 空白 deviceId 和 beforeTime 等同于无条件")
    void deleteBatch_withBlankConditions_deletesAll() {
        when(sensorDataMapper.delete(any())).thenReturn(50);

        int deleted = sensorDataService.deleteBatch("  ", "  ");

        assertEquals(50, deleted);
    }

    // ==================== deleteByDeviceId() ====================

    @Test
    @DisplayName("deleteByDeviceId() 按 deviceId 删除全部传感器数据")
    void deleteByDeviceId_deletesByDeviceId() {
        when(sensorDataMapper.delete(any())).thenReturn(10);

        sensorDataService.deleteByDeviceId("dev-001");

        verify(sensorDataMapper).delete(any());
    }

    // ==================== count() ====================

    @Test
    @DisplayName("count() 返回传感器数据总数")
    void count_returnsTotalCount() {
        when(sensorDataMapper.selectCount(any())).thenReturn(1000L);

        assertEquals(1000L, sensorDataService.count());
    }
}
