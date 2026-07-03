package com.smartmushroom.controller;

import com.smartmushroom.entity.DeviceCommand;
import com.smartmushroom.mapper.DeviceCommandMapper;
import com.smartmushroom.mapper.DeviceInfoMapper;
import com.smartmushroom.mapper.SensorDataMapper;
import com.smartmushroom.service.CommandService;
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
 * {@link CommandController} 切片测试。
 * 重点验证 limit 钳制（1–200）、CRUD 响应格式。
 */
@DisplayName("CommandController - 命令管理接口")
@WebMvcTest(CommandController.class)
class CommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommandService commandService;
    @MockBean
    private DeviceInfoMapper deviceInfoMapper;
    @MockBean
    private SensorDataMapper sensorDataMapper;
    @MockBean
    private DeviceCommandMapper deviceCommandMapper;

    // ==================== GET /commands ====================

    @Test
    @DisplayName("GET /commands limit=50 正常查询")
    void list_normalLimit_returnsCommands() throws Exception {
        DeviceCommand cmd = new DeviceCommand();
        cmd.setId(1L);
        cmd.setCommand("reboot");
        when(commandService.list(eq(50), any())).thenReturn(List.of(cmd));

        mockMvc.perform(get("/api/v1/commands").param("limit", "50"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data[0].command").value("reboot"))
                .andExpect(jsonPath("$.total").value(1));
    }

    @Test
    @DisplayName("GET /commands limit=0 钳制为 1")
    void list_limitZero_clampedToOne() throws Exception {
        when(commandService.list(eq(1), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/commands").param("limit", "0"))
                .andExpect(status().isOk());

        verify(commandService).list(eq(1), any());
    }

    @Test
    @DisplayName("GET /commands limit=9999 钳制为 200")
    void list_limitExceedsMax_clampedToTwoHundred() throws Exception {
        when(commandService.list(eq(200), any())).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/commands").param("limit", "9999"))
                .andExpect(status().isOk());

        verify(commandService).list(eq(200), any());
    }

    @Test
    @DisplayName("GET /commands 带 device_id 过滤")
    void list_withDeviceId_returnsFiltered() throws Exception {
        when(commandService.list(eq(50), eq("dev-001"))).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/commands").param("device_id", "dev-001"))
                .andExpect(status().isOk());

        verify(commandService).list(eq(50), eq("dev-001"));
    }

    // ==================== POST /commands ====================

    @Test
    @DisplayName("POST /commands 插入命令成功返回 ID")
    void insert_returnsId() throws Exception {
        when(commandService.insert(anyString(), anyString(), anyString(), anyString())).thenReturn(10L);

        mockMvc.perform(post("/api/v1/commands")
                        .param("command", "set_temp")
                        .param("device_id", "dev-001")
                        .param("param_key", "target")
                        .param("param_value", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.id").value(10));
    }

    // ==================== PUT /commands/{id} ====================

    @Test
    @DisplayName("PUT /commands/{id} 更新状态成功返回 code=0")
    void updateStatus_success_returnsOk() throws Exception {
        when(commandService.updateStatus(eq(1L), eq("executed"))).thenReturn(true);

        mockMvc.perform(put("/api/v1/commands/1").param("status", "executed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("PUT /commands/{id} 命令不存在返回 code=-1")
    void updateStatus_notFound_returnsFail() throws Exception {
        when(commandService.updateStatus(eq(999L), anyString())).thenReturn(false);

        mockMvc.perform(put("/api/v1/commands/999").param("status", "failed"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("命令记录不存在"));
    }

    // ==================== DELETE /commands/{id} ====================

    @Test
    @DisplayName("DELETE /commands/{id} 删除成功返回 code=0")
    void delete_success_returnsOk() throws Exception {
        when(commandService.delete(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/commands/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));
    }

    @Test
    @DisplayName("DELETE /commands/{id} 命令不存在返回 code=-1")
    void delete_notFound_returnsFail() throws Exception {
        when(commandService.delete(999L)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/commands/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("命令记录不存在"));
    }
}
