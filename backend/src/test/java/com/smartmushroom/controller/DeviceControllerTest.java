package com.smartmushroom.controller;

import com.smartmushroom.entity.DeviceInfo;
import com.smartmushroom.mapper.DeviceCommandMapper;
import com.smartmushroom.mapper.DeviceInfoMapper;
import com.smartmushroom.mapper.SensorDataMapper;
import com.smartmushroom.service.DeviceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link DeviceController} 切片测试。
 * 覆盖设备 CRUD 全流程、设备不存在时返回 fail。
 */
@DisplayName("DeviceController - 设备管理接口")
@WebMvcTest(DeviceController.class)
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;
    @MockBean
    private DeviceInfoMapper deviceInfoMapper;
    @MockBean
    private SensorDataMapper sensorDataMapper;
    @MockBean
    private DeviceCommandMapper deviceCommandMapper;

    // ==================== POST /devices ====================

    @Test
    @DisplayName("POST /devices 创建设备成功返回设备信息")
    void create_success_returnsDevice() throws Exception {
        DeviceInfo device = new DeviceInfo();
        device.setDeviceId("dev-001");
        device.setDeviceName("传感器A");
        device.setStatus("offline");
        when(deviceService.create(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(device);

        mockMvc.perform(post("/api/v1/devices")
                        .param("device_id", "dev-001")
                        .param("device_name", "传感器A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.device_id").value("dev-001"))
                .andExpect(jsonPath("$.data.device_name").value("传感器A"));
    }

    @Test
    @DisplayName("POST /devices Service 抛异常时返回 code=-1")
    void create_serviceThrows_returnsFail() throws Exception {
        when(deviceService.create(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("数据库连接失败"));

        mockMvc.perform(post("/api/v1/devices").param("device_id", "dev-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("设备注册失败: 数据库连接失败"));
    }

    // ==================== GET /devices ====================

    @Test
    @DisplayName("GET /devices 返回全部设备列表")
    void list_returnsAllDevices() throws Exception {
        DeviceInfo d1 = new DeviceInfo();
        d1.setDeviceId("dev-001");
        DeviceInfo d2 = new DeviceInfo();
        d2.setDeviceId("dev-002");
        when(deviceService.listAll()).thenReturn(List.of(d1, d2));

        mockMvc.perform(get("/api/v1/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.total").value(2));
    }

    // ==================== GET /devices/{deviceId} ====================

    @Test
    @DisplayName("GET /devices/{deviceId} 设备存在时返回设备信息")
    void get_deviceExists_returnsDevice() throws Exception {
        DeviceInfo device = new DeviceInfo();
        device.setDeviceId("dev-001");
        device.setDeviceName("传感器A");
        when(deviceService.getByDeviceId("dev-001")).thenReturn(device);

        mockMvc.perform(get("/api/v1/devices/dev-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.device_id").value("dev-001"));
    }

    @Test
    @DisplayName("GET /devices/{deviceId} 设备不存在时返回 code=-1")
    void get_deviceNotFound_returnsFail() throws Exception {
        when(deviceService.getByDeviceId("not-exist")).thenReturn(null);

        mockMvc.perform(get("/api/v1/devices/not-exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("设备不存在"));
    }

    // ==================== PUT /devices/{deviceId} ====================

    @Test
    @DisplayName("PUT /devices/{deviceId} 更新成功返回 code=0")
    void update_success_returnsOk() throws Exception {
        when(deviceService.update(eq("dev-001"), anyMap())).thenReturn(true);

        mockMvc.perform(put("/api/v1/devices/dev-001")
                        .param("device_name", "新名称")
                        .param("status", "online"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("PUT /devices/{deviceId} 设备不存在返回 code=-1")
    void update_notFound_returnsFail() throws Exception {
        when(deviceService.update(eq("not-exist"), anyMap())).thenReturn(false);

        mockMvc.perform(put("/api/v1/devices/not-exist").param("status", "online"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("设备不存在或未做修改"));
    }

    // ==================== DELETE /devices/{deviceId} ====================

    @Test
    @DisplayName("DELETE /devices/{deviceId} 删除成功返回 code=0")
    void delete_success_returnsOk() throws Exception {
        when(deviceService.delete("dev-001")).thenReturn(true);

        mockMvc.perform(delete("/api/v1/devices/dev-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("DELETE /devices/{deviceId} 设备不存在返回 code=-1")
    void delete_notFound_returnsFail() throws Exception {
        when(deviceService.delete("not-exist")).thenReturn(false);

        mockMvc.perform(delete("/api/v1/devices/not-exist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("设备不存在"));
    }
}
