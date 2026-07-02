package com.smartmushroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartmushroom.entity.DeviceCommand;
import com.smartmushroom.mapper.DeviceCommandMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final DeviceCommandMapper deviceCommandMapper;

    public Long insert(String deviceId, String command, String paramKey, String paramValue) {
        DeviceCommand row = new DeviceCommand();
        row.setDeviceId(deviceId != null ? deviceId : "roomone");
        row.setCommand(command);
        row.setParamKey(paramKey != null ? paramKey : "");
        row.setParamValue(paramValue != null ? paramValue : "");
        row.setStatus("pending");
        row.setCreateTime(now());
        row.setUpdateTime(now());
        deviceCommandMapper.insert(row);
        return row.getId();
    }

    public List<DeviceCommand> list(int limit, String deviceId) {
        LambdaQueryWrapper<DeviceCommand> wrapper = new LambdaQueryWrapper<>();
        if (deviceId != null && !deviceId.isBlank()) {
            wrapper.eq(DeviceCommand::getDeviceId, deviceId);
        }
        wrapper.orderByDesc(DeviceCommand::getId).last("LIMIT " + limit);
        return deviceCommandMapper.selectList(wrapper);
    }

    public boolean updateStatus(Long id, String status) {
        LambdaUpdateWrapper<DeviceCommand> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(DeviceCommand::getId, id)
                .set(DeviceCommand::getStatus, status)
                .set(DeviceCommand::getUpdateTime, now());
        return deviceCommandMapper.update(null, wrapper) > 0;
    }

    public boolean delete(Long id) {
        return deviceCommandMapper.deleteById(id) > 0;
    }

    public void deleteByDeviceId(String deviceId) {
        deviceCommandMapper.delete(
                new LambdaQueryWrapper<DeviceCommand>().eq(DeviceCommand::getDeviceId, deviceId));
    }

    public long count() {
        return deviceCommandMapper.selectCount(null);
    }

    private String now() {
        return LocalDateTime.now().format(FMT);
    }
}
