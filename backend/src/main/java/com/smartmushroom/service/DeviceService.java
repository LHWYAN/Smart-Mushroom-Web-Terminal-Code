package com.smartmushroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartmushroom.entity.DeviceInfo;
import com.smartmushroom.mapper.DeviceInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DeviceInfoMapper deviceInfoMapper;
    private final SensorDataService sensorDataService;
    private final CommandService commandService;

    public DeviceInfo create(String deviceId, String deviceName, String deviceType,
                             String location, String remarks) {
        DeviceInfo device = new DeviceInfo();
        device.setDeviceId(deviceId);
        device.setDeviceName(deviceName != null ? deviceName : "");
        device.setDeviceType(deviceType != null ? deviceType : "sensor");
        device.setLocation(location != null ? location : "");
        device.setStatus("offline");
        device.setRemarks(remarks != null ? remarks : "");
        device.setCreateTime(now());
        device.setUpdateTime(now());
        deviceInfoMapper.insert(device);
        return device;
    }

    public List<DeviceInfo> listAll() {
        return deviceInfoMapper.selectList(
                new LambdaQueryWrapper<DeviceInfo>().orderByAsc(DeviceInfo::getId));
    }

    public DeviceInfo getByDeviceId(String deviceId) {
        return deviceInfoMapper.selectOne(
                new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getDeviceId, deviceId));
    }

    public boolean update(String deviceId, Map<String, String> fields) {
        LambdaUpdateWrapper<DeviceInfo> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(DeviceInfo::getDeviceId, deviceId);
        boolean changed = false;

        if (fields.containsKey("device_name")) {
            wrapper.set(DeviceInfo::getDeviceName, fields.get("device_name"));
            changed = true;
        }
        if (fields.containsKey("device_type")) {
            wrapper.set(DeviceInfo::getDeviceType, fields.get("device_type"));
            changed = true;
        }
        if (fields.containsKey("location")) {
            wrapper.set(DeviceInfo::getLocation, fields.get("location"));
            changed = true;
        }
        if (fields.containsKey("status")) {
            wrapper.set(DeviceInfo::getStatus, fields.get("status"));
            changed = true;
        }
        if (fields.containsKey("remarks")) {
            wrapper.set(DeviceInfo::getRemarks, fields.get("remarks"));
            changed = true;
        }
        if (!changed) {
            return false;
        }
        wrapper.set(DeviceInfo::getUpdateTime, now());
        return deviceInfoMapper.update(null, wrapper) > 0;
    }

    @Transactional
    public boolean delete(String deviceId) {
        sensorDataService.deleteByDeviceId(deviceId);
        commandService.deleteByDeviceId(deviceId);
        return deviceInfoMapper.delete(
                new LambdaQueryWrapper<DeviceInfo>().eq(DeviceInfo::getDeviceId, deviceId)) > 0;
    }

    public void markOnline(String deviceId) {
        update(deviceId, Map.of("status", "online"));
    }

    public long count() {
        return deviceInfoMapper.selectCount(null);
    }

    private String now() {
        return LocalDateTime.now().format(FMT);
    }
}
