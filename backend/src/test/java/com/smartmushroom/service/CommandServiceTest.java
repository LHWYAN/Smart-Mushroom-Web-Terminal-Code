package com.smartmushroom.service;

import com.smartmushroom.entity.DeviceCommand;
import com.smartmushroom.mapper.DeviceCommandMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link CommandService} 单元测试。
 * 使用 Mockito Mock DeviceCommandMapper，验证命令 CRUD、状态更新、级联清理。
 */
@DisplayName("CommandService - 命令管理")
@ExtendWith(MockitoExtension.class)
class CommandServiceTest {

    @BeforeAll
    static void initMybatisPlus() {
        MybatisPlusTestSupport.initTableInfo(DeviceCommand.class);
    }

    @Mock
    private DeviceCommandMapper deviceCommandMapper;

    @InjectMocks
    private CommandService commandService;

    // ==================== insert() ====================

    @Test
    @DisplayName("insert() 完整参数插入，返回自增 ID")
    void insert_withFullParams_insertsAndReturnsId() {
        when(deviceCommandMapper.insert(any(DeviceCommand.class))).thenAnswer(invocation -> {
            DeviceCommand row = invocation.getArgument(0);
            row.setId(42L);
            return 1;
        });

        Long id = commandService.insert("dev-001", "set_temp", "target", "25");

        assertEquals(42L, id);
        ArgumentCaptor<DeviceCommand> captor = ArgumentCaptor.forClass(DeviceCommand.class);
        verify(deviceCommandMapper).insert(captor.capture());
        DeviceCommand inserted = captor.getValue();
        assertEquals("dev-001", inserted.getDeviceId());
        assertEquals("set_temp", inserted.getCommand());
        assertEquals("target", inserted.getParamKey());
        assertEquals("25", inserted.getParamValue());
        assertEquals("pending", inserted.getStatus());
        assertNotNull(inserted.getCreateTime());
        assertNotNull(inserted.getUpdateTime());
    }

    @Test
    @DisplayName("insert() null 参数使用默认值（deviceId=roomone, paramKey/paramValue=空串）")
    void insert_withNullParams_usesDefaults() {
        when(deviceCommandMapper.insert(any(DeviceCommand.class))).thenReturn(1);

        Long id = commandService.insert(null, "reboot", null, null);

        ArgumentCaptor<DeviceCommand> captor = ArgumentCaptor.forClass(DeviceCommand.class);
        verify(deviceCommandMapper).insert(captor.capture());
        DeviceCommand inserted = captor.getValue();
        assertEquals("roomone", inserted.getDeviceId());
        assertEquals("", inserted.getParamKey());
        assertEquals("", inserted.getParamValue());
        assertEquals("pending", inserted.getStatus());
    }

    // ==================== list() ====================

    @Test
    @DisplayName("list() 带 deviceId 过滤，返回命令列表")
    void list_withDeviceId_returnsCommands() {
        DeviceCommand cmd1 = new DeviceCommand();
        cmd1.setDeviceId("dev-001");
        cmd1.setCommand("reboot");
        when(deviceCommandMapper.selectList(any())).thenReturn(List.of(cmd1));

        List<DeviceCommand> result = commandService.list(10, "dev-001");

        assertEquals(1, result.size());
        assertEquals("reboot", result.get(0).getCommand());
    }

    @Test
    @DisplayName("list() 不带 deviceId（null）查询全部命令")
    void list_withoutDeviceId_returnsAll() {
        when(deviceCommandMapper.selectList(any())).thenReturn(List.of());

        List<DeviceCommand> result = commandService.list(50, null);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("list() 空白 deviceId 等同于不过滤")
    void list_withBlankDeviceId_returnsAll() {
        when(deviceCommandMapper.selectList(any())).thenReturn(List.of());

        List<DeviceCommand> result = commandService.list(20, "  ");

        assertTrue(result.isEmpty());
    }

    // ==================== updateStatus() ====================

    @Test
    @DisplayName("updateStatus() 更新成功返回 true")
    void updateStatus_success_returnsTrue() {
        when(deviceCommandMapper.update(eq(null), any())).thenReturn(1);

        boolean result = commandService.updateStatus(1L, "executed");

        assertTrue(result);
    }

    @Test
    @DisplayName("updateStatus() 命令不存在返回 false")
    void updateStatus_notFound_returnsFalse() {
        when(deviceCommandMapper.update(eq(null), any())).thenReturn(0);

        boolean result = commandService.updateStatus(999L, "failed");

        assertFalse(result);
    }

    // ==================== delete() ====================

    @Test
    @DisplayName("delete() 删除成功返回 true")
    void delete_success_returnsTrue() {
        when(deviceCommandMapper.deleteById(1L)).thenReturn(1);

        assertTrue(commandService.delete(1L));
    }

    @Test
    @DisplayName("delete() 命令不存在返回 false")
    void delete_notFound_returnsFalse() {
        when(deviceCommandMapper.deleteById(999L)).thenReturn(0);

        assertFalse(commandService.delete(999L));
    }

    // ==================== deleteByDeviceId() ====================

    @Test
    @DisplayName("deleteByDeviceId() 级联清理指定设备的所有命令")
    void deleteByDeviceId_deletesByDeviceId() {
        when(deviceCommandMapper.delete(any())).thenReturn(5);

        commandService.deleteByDeviceId("dev-001");

        verify(deviceCommandMapper).delete(any());
    }

    // ==================== count() ====================

    @Test
    @DisplayName("count() 返回命令总数")
    void count_returnsTotalCount() {
        when(deviceCommandMapper.selectCount(any())).thenReturn(30L);

        assertEquals(30L, commandService.count());
    }
}
