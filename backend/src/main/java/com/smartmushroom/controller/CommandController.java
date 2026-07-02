package com.smartmushroom.controller;

import com.smartmushroom.dto.ApiResponse;
import com.smartmushroom.entity.DeviceCommand;
import com.smartmushroom.service.CommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "设备命令")
@RestController
@RequestMapping("/api/v1/commands")
@RequiredArgsConstructor
public class CommandController {

    private final CommandService commandService;

    @Operation(summary = "插入命令记录")
    @PostMapping
    public ApiResponse<Map<String, Long>> insert(
            @RequestParam(defaultValue = "roomone") String device_id,
            @RequestParam String command,
            @RequestParam(defaultValue = "") String param_key,
            @RequestParam(defaultValue = "") String param_value) {
        Long id = commandService.insert(device_id, command, param_key, param_value);
        return ApiResponse.ok(Map.of("id", id));
    }

    @Operation(summary = "查询命令日志")
    @GetMapping
    public ApiResponse<List<DeviceCommand>> list(
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(required = false) String device_id) {
        int safeLimit = Math.min(Math.max(limit, 1), 200);
        List<DeviceCommand> rows = commandService.list(safeLimit, device_id);
        return ApiResponse.ok(rows, rows.size());
    }

    @Operation(summary = "更新命令状态")
    @PutMapping("/{id}")
    public ApiResponse<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        if (commandService.updateStatus(id, status)) {
            return ApiResponse.ok(null);
        }
        return ApiResponse.fail("命令记录不存在");
    }

    @Operation(summary = "删除命令记录")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (commandService.delete(id)) {
            return ApiResponse.ok(null);
        }
        return ApiResponse.fail("命令记录不存在");
    }
}
