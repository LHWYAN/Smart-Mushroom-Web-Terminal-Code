package com.smartmushroom.controller;

import com.smartmushroom.dto.ApiResponse;
import com.smartmushroom.entity.DeviceInfo;
import com.smartmushroom.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "设备管理")
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @Operation(summary = "注册新设备")
    @PostMapping
    public ApiResponse<DeviceInfo> create(
            @RequestParam String device_id,
            @RequestParam(defaultValue = "") String device_name,
            @RequestParam(defaultValue = "sensor") String device_type,
            @RequestParam(defaultValue = "") String location,
            @RequestParam(defaultValue = "") String remarks) {
        try {
            return ApiResponse.ok(deviceService.create(device_id, device_name, device_type, location, remarks));
        } catch (Exception e) {
            return ApiResponse.fail("设备注册失败: " + e.getMessage());
        }
    }

    @Operation(summary = "查询所有设备")
    @GetMapping
    public ApiResponse<List<DeviceInfo>> list() {
        List<DeviceInfo> devices = deviceService.listAll();
        return ApiResponse.ok(devices, devices.size());
    }

    @Operation(summary = "查询单个设备")
    @GetMapping("/{deviceId}")
    public ApiResponse<DeviceInfo> get(@PathVariable String deviceId) {
        DeviceInfo device = deviceService.getByDeviceId(deviceId);
        if (device == null) {
            return ApiResponse.fail("设备不存在");
        }
        return ApiResponse.ok(device);
    }

    @Operation(summary = "更新设备信息")
    @PutMapping("/{deviceId}")
    public ApiResponse<Void> update(
            @PathVariable String deviceId,
            @RequestParam(required = false) String device_name,
            @RequestParam(required = false) String device_type,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String remarks) {
        Map<String, String> fields = new HashMap<>();
        if (device_name != null) fields.put("device_name", device_name);
        if (device_type != null) fields.put("device_type", device_type);
        if (location != null) fields.put("location", location);
        if (status != null) fields.put("status", status);
        if (remarks != null) fields.put("remarks", remarks);

        if (deviceService.update(deviceId, fields)) {
            return ApiResponse.ok(null);
        }
        return ApiResponse.fail("设备不存在或未做修改");
    }

    @Operation(summary = "删除设备")
    @DeleteMapping("/{deviceId}")
    public ApiResponse<Void> delete(@PathVariable String deviceId) {
        if (deviceService.delete(deviceId)) {
            return ApiResponse.ok(null);
        }
        return ApiResponse.fail("设备不存在");
    }
}
