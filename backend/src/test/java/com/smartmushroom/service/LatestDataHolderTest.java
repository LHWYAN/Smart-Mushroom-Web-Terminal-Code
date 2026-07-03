package com.smartmushroom.service;

import com.smartmushroom.entity.SensorData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link LatestDataHolder} 单元测试。
 * 覆盖初始状态、update 后读取、多次 update 覆盖旧值。
 */
@DisplayName("LatestDataHolder - 最新数据缓存")
class LatestDataHolderTest {

    private LatestDataHolder holder;

    @BeforeEach
    void setUp() {
        holder = new LatestDataHolder();
    }

    @Test
    @DisplayName("初始状态 getLatest() 返回 null")
    void getLatest_initial_returnsNull() {
        assertNull(holder.getLatest(), "初始状态下应返回 null");
    }

    @Test
    @DisplayName("update() 后 getLatest() 返回最新对象")
    void update_thenGetLatest_returnsUpdatedData() {
        SensorData data = new SensorData();
        data.setDeviceId("sensor-001");
        data.setTemp("22.5");
        data.setCo2("450");

        holder.update(data);

        SensorData result = holder.getLatest();
        assertNotNull(result, "update 后不应为 null");
        assertEquals("sensor-001", result.getDeviceId());
        assertEquals("22.5", result.getTemp());
        assertEquals("450", result.getCo2());
    }

    @Test
    @DisplayName("多次 update 覆盖旧值，getLatest() 始终返回最后一次")
    void multipleUpdates_overwritesOldValue() {
        SensorData first = new SensorData();
        first.setDeviceId("device-A");
        first.setTemp("20.0");

        SensorData second = new SensorData();
        second.setDeviceId("device-B");
        second.setTemp("25.0");

        SensorData third = new SensorData();
        third.setDeviceId("device-C");
        third.setTemp("30.0");

        holder.update(first);
        assertEquals("device-A", holder.getLatest().getDeviceId(), "第一次 update 后应返回 device-A");

        holder.update(second);
        assertEquals("device-B", holder.getLatest().getDeviceId(), "第二次 update 后应返回 device-B");

        holder.update(third);
        assertEquals("device-C", holder.getLatest().getDeviceId(), "第三次 update 后应返回 device-C");
    }

    @Test
    @DisplayName("update(null) 后 getLatest() 返回 null")
    void updateNull_thenGetLatest_returnsNull() {
        SensorData data = new SensorData();
        data.setDeviceId("sensor-001");
        holder.update(data);
        assertNotNull(holder.getLatest());

        holder.update(null);
        assertNull(holder.getLatest(), "update(null) 后应返回 null");
    }
}
