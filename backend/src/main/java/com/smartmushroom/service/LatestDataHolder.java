package com.smartmushroom.service;

import com.smartmushroom.entity.SensorData;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
public class LatestDataHolder {

    private final AtomicReference<SensorData> latest = new AtomicReference<>();

    public void update(SensorData data) {
        latest.set(data);
    }

    public SensorData getLatest() {
        return latest.get();
    }
}
