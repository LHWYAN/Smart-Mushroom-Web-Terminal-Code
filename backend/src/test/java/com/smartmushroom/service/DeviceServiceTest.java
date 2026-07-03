package com.smartmushroom.service;

import com.smartmushroom.entity.DeviceInfo;
import com.smartmushroom.mapper.DeviceInfoMapper;
import com.smartmushroom.test.MybatisPlusTestSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link DeviceService} 单元测试。
 * 使用 Mockito Mock DeviceInfoMapper 及级联的 SensorDataService、CommandService。
 */
@DisplayName("DeviceService - 设备管理")
@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @BeforeAll
    static void initMybatisPlus() {
        MybatisPlusTestSupport.initTableInfo(DeviceInfo.class);
    }

    @Mock
    private DeviceInfoMapper deviceInfoMapper;
    @Mock
    private SensorDataService sensorDataService;
    @Mock
    private CommandService commandService;

    @InjectMocks
    private DeviceService deviceService;

    // ==================== create() ====================

    @Test
    @DisplayName("create() 正常创建：所有字段传入")
    void create_withAllFields_insertsDevice() {
        when(deviceInfoMapper.insert(any(DeviceInfo.class))).thenReturn(1);

        DeviceInfo result = deviceService.create("dev-001", "温度传感器", "sensor", "1号棚", "测试设备");

        assertNotNull(result);
        assertEquals("dev-001", result.getDeviceId());
        assertEquals("温度传感器", result.getDeviceName());
        assertEquals("sensor", result.getDeviceType());
        assertEquals("1号棚", result.getLocation());
        assertEquals("offline", result.getStatus());
        assertEquals("测试设备", result.getRemarks());
        assertNotNull(result.getCreateTime());
        assertNotNull(result.getUpdateTime());
        verify(deviceInfoMapper).insert(any(DeviceInfo.class));
    }

    @Test
    @DisplayName("create() null 字段使用默认值：deviceType=sensor, status=offline")
    void create_withNullFields_usesDefaults() {
        when(deviceInfoMapper.insert(any(DeviceInfo.class))).thenReturn(1);

        DeviceInfo result = deviceService.create("dev-002", null, null, null, null);

        assertEquals("", result.getDeviceName());
        assertEquals("sensor", result.getDeviceType());
        assertEquals("", result.getLocation());
        assertEquals("offline", result.getStatus());
        assertEquals("", result.getRemarks());
    }

    // ==================== listAll() ====================

    @Test
    @DisplayName("listAll() 返回全部设备列表")
    void listAll_returnsDeviceList() {
        DeviceInfo d1 = new DeviceInfo();
        d1.setDeviceId("dev-001");
        DeviceInfo d2 = new DeviceInfo();
        d2.setDeviceId("dev-002");
        when(deviceInfoMapper.selectList(any())).thenReturn(List.of(d1, d2));

        List<DeviceInfo> result = deviceService.listAll();

        assertEquals(2, result.size());
        assertEquals("dev-001", result.get(0).getDeviceId());
    }

    @Test
    @DisplayName("listAll() 无设备时返回空列表")
    void listAll_noDevices_returnsEmptyList() {
        when(deviceInfoMapper.selectList(any())).thenReturn(List.of());

        List<DeviceInfo> result = deviceService.listAll();

        assertTrue(result.isEmpty());
    }

    // ==================== getByDeviceId() ====================

    @Test
    @DisplayName("getByDeviceId() 设备存在时返回设备")
    void getByDeviceId_exists_returnsDevice() {
        DeviceInfo device = new DeviceInfo();
        device.setDeviceId("dev-001");
        device.setDeviceName("传感器A");
        when(deviceInfoMapper.selectOne(any())).thenReturn(device);

        DeviceInfo result = deviceService.getByDeviceId("dev-001");

        assertNotNull(result);
        assertEquals("dev-001", result.getDeviceId());
        assertEquals("传感器A", result.getDeviceName());
    }

    @Test
    @DisplayName("getByDeviceId() 设备不存在时返回 null")
    void getByDeviceId_notExists_returnsNull() {
        when(deviceInfoMapper.selectOne(any())).thenReturn(null);

        DeviceInfo result = deviceService.getByDeviceId("not-exist");

        assertNull(result);
    }

    // ==================== update() ====================

    @Test
    @DisplayName("update() 有效字段更新返回 true")
    void update_withValidFields_returnsTrue() {
        when(deviceInfoMapper.update(eq(null), any())).thenReturn(1);

        boolean result = deviceService.update("dev-001", Map.of("device_name", "新名称", "status", "online"));

        assertTrue(result);
        verify(deviceInfoMapper).update(eq(null), any());
    }

    @Test
    @DisplayName("update() 无有效字段返回 false")
    void update_withNoValidFields_returnsFalse() {
        boolean result = deviceService.update("dev-001", Map.of("unknown_field", "value"));

        assertFalse(result);
        verify(deviceInfoMapper, never()).update(any(), any());
    }

    @Test
    @DisplayName("update() 数据库返回 0 行受影响时返回 false")
    void update_zeroRowsAffected_returnsFalse() {
        when(deviceInfoMapper.update(eq(null), any())).thenReturn(0);

        boolean result = deviceService.update("dev-001", Map.of("status", "online"));

        assertFalse(result);
    }

    // ==================== delete() ====================

    @Test
    @DisplayName("delete() 验证级联调用 sensorDataService 和 commandService，并返回 true")
    void delete_cascadesAndReturnsTrue() {
        when(deviceInfoMapper.delete(any())).thenReturn(1);

        boolean result = deviceService.delete("dev-001");

        assertTrue(result);
        verify(sensorDataService).deleteByDeviceId("dev-001");
        verify(commandService).deleteByDeviceId("dev-001");
        verify(deviceInfoMapper).delete(any());
    }

    @Test
    @DisplayName("delete() 设备不存在时级联仍执行，返回 false")
    void delete_deviceNotFound_cascadesStillExecute_returnsFalse() {
        when(deviceInfoMapper.delete(any())).thenReturn(0);

        boolean result = deviceService.delete("not-exist");

        assertFalse(result);
        verify(sensorDataService).deleteByDeviceId("not-exist");
        verify(commandService).deleteByDeviceId("not-exist");
    }

    // ==================== markOnline() ====================

    @Test
    @DisplayName("markOnline() 通过 update 方法将 status 设为 online")
    void markOnline_setsStatusToOnline() {
        when(deviceInfoMapper.update(eq(null), any())).thenReturn(1);

        deviceService.markOnline("dev-001");

        verify(deviceInfoMapper).update(eq(null), any());
    }

    // ==================== count() ====================

    @Test
    @DisplayName("count() 返回设备总数")
    void count_returnsTotalCount() {
        when(deviceInfoMapper.selectCount(any())).thenReturn(5L);

        long result = deviceService.count();

        assertEquals(5L, result);
    }
}
