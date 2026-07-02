package com.smartmushroom.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.smartmushroom.entity.SensorData;
import com.smartmushroom.mapper.SensorDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SensorDataService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SensorDataMapper sensorDataMapper;

    public Long insert(Map<String, String> data) {
        SensorData row = new SensorData();
        row.setDeviceId(data.getOrDefault("device_id", "roomone"));
        row.setTemp(data.getOrDefault("Temp", ""));
        row.setHumi(data.getOrDefault("Humi", ""));
        row.setLumi(data.getOrDefault("Lumi", ""));
        row.setLampST(data.getOrDefault("LampST", "OFF"));
        row.setCondST(data.getOrDefault("CondST", "OFF"));
        row.setVentST(data.getOrDefault("VentST", "OFF"));
        row.setBuzzerST(data.getOrDefault("BuzzerST", "OFF"));
        row.setSmoke(data.getOrDefault("Smoke", "0"));
        row.setCo2(data.getOrDefault("CO2", "400"));
        row.setCreateTime(now());
        sensorDataMapper.insert(row);
        return row.getId();
    }

    public SensorData queryLatestOne(String deviceId) {
        LambdaQueryWrapper<SensorData> wrapper = new LambdaQueryWrapper<>();
        if (deviceId != null && !deviceId.isBlank()) {
            wrapper.eq(SensorData::getDeviceId, deviceId);
        }
        wrapper.orderByDesc(SensorData::getId).last("LIMIT 1");
        return sensorDataMapper.selectOne(wrapper);
    }

    public List<SensorData> queryHistory(int limit, int offset, String deviceId,
                                         String startTime, String endTime) {
        LambdaQueryWrapper<SensorData> wrapper = new LambdaQueryWrapper<>();
        if (deviceId != null && !deviceId.isBlank()) {
            wrapper.eq(SensorData::getDeviceId, deviceId);
        }
        if (startTime != null && !startTime.isBlank()) {
            wrapper.ge(SensorData::getCreateTime, startTime);
        }
        if (endTime != null && !endTime.isBlank()) {
            wrapper.le(SensorData::getCreateTime, endTime);
        }
        wrapper.orderByDesc(SensorData::getId).last("LIMIT " + limit + " OFFSET " + offset);
        return sensorDataMapper.selectList(wrapper);
    }

    public boolean update(Long id, Map<String, String> fields) {
        LambdaUpdateWrapper<SensorData> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SensorData::getId, id);
        boolean changed = false;

        Map<String, java.util.function.Consumer<String>> mapping = new HashMap<>();
        mapping.put("Temp", v -> wrapper.set(SensorData::getTemp, v));
        mapping.put("Humi", v -> wrapper.set(SensorData::getHumi, v));
        mapping.put("Lumi", v -> wrapper.set(SensorData::getLumi, v));
        mapping.put("LampST", v -> wrapper.set(SensorData::getLampST, v));
        mapping.put("CondST", v -> wrapper.set(SensorData::getCondST, v));
        mapping.put("VentST", v -> wrapper.set(SensorData::getVentST, v));
        mapping.put("BuzzerST", v -> wrapper.set(SensorData::getBuzzerST, v));
        mapping.put("Smoke", v -> wrapper.set(SensorData::getSmoke, v));
        mapping.put("CO2", v -> wrapper.set(SensorData::getCo2, v));

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            if (mapping.containsKey(entry.getKey()) && entry.getValue() != null) {
                mapping.get(entry.getKey()).accept(entry.getValue());
                changed = true;
            }
        }
        if (!changed) {
            return false;
        }
        return sensorDataMapper.update(null, wrapper) > 0;
    }

    public boolean delete(Long id) {
        return sensorDataMapper.deleteById(id) > 0;
    }

    public int deleteBatch(String deviceId, String beforeTime) {
        LambdaQueryWrapper<SensorData> wrapper = new LambdaQueryWrapper<>();
        if (deviceId != null && !deviceId.isBlank()) {
            wrapper.eq(SensorData::getDeviceId, deviceId);
        }
        if (beforeTime != null && !beforeTime.isBlank()) {
            wrapper.lt(SensorData::getCreateTime, beforeTime);
        }
        return sensorDataMapper.delete(wrapper);
    }

    public void deleteByDeviceId(String deviceId) {
        sensorDataMapper.delete(new LambdaQueryWrapper<SensorData>().eq(SensorData::getDeviceId, deviceId));
    }

    public long count() {
        return sensorDataMapper.selectCount(null);
    }

    private String now() {
        return LocalDateTime.now().format(FMT);
    }
}
