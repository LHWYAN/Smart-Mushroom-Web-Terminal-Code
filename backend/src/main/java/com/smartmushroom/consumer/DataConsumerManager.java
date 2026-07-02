package com.smartmushroom.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmushroom.config.AppProperties;
import com.smartmushroom.entity.SensorData;
import com.smartmushroom.service.DeviceService;
import com.smartmushroom.service.LatestDataHolder;
import com.smartmushroom.service.SensorDataService;
import com.smartmushroom.websocket.RealtimeWebSocketHandler;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.qpid.jms.JmsConnectionFactory;
import org.springframework.stereotype.Component;

import jakarta.jms.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataConsumerManager {

    private final AppProperties appProperties;
    private final SensorDataService sensorDataService;
    private final DeviceService deviceService;
    private final LatestDataHolder latestDataHolder;
    private final RealtimeWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final Random random = new Random();

    private ScheduledExecutorService scheduler;
    private Connection amqpConnection;
    private Thread amqpThread;

    @PostConstruct
    public void start() {
        if (running.compareAndSet(false, true)) {
            String source = appProperties.getDataSource().toLowerCase();
            log.info("数据源类型: {}", source);
            if ("amqp".equals(source)) {
                startAmqpConsumer();
            } else {
                startSimulateConsumer();
            }
        }
    }

    @PreDestroy
    public void stop() {
        running.set(false);
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
        if (amqpConnection != null) {
            try {
                amqpConnection.close();
            } catch (JMSException ignored) {
            }
        }
        if (amqpThread != null) {
            amqpThread.interrupt();
        }
    }

    private void startSimulateConsumer() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Map<String, String> data = simulateOnce();
                persistData(data, true);
            } catch (Exception e) {
                log.error("模拟数据存储失败: {}", e.getMessage());
            }
        }, 0, appProperties.getSimulateInterval(), TimeUnit.SECONDS);
        log.info("模拟数据源已启动，间隔 {} 秒", appProperties.getSimulateInterval());
    }

    private Map<String, String> simulateOnce() {
        double temp = 26.5 + random.nextDouble() * 3;
        double humi = 55 + random.nextDouble() * 10;
        int lumi = 200 + random.nextInt(300);
        Map<String, String> data = new HashMap<>();
        data.put("device_id", "roomone");
        data.put("Temp", String.format("%.1f", temp));
        data.put("Humi", String.format("%.1f", humi));
        data.put("Lumi", String.valueOf(lumi));
        data.put("LampST", lumi < 50 ? "ON" : "OFF");
        data.put("CondST", "OFF");
        data.put("VentST", "OFF");
        data.put("BuzzerST", "OFF");
        data.put("Smoke", String.valueOf(random.nextInt(5)));
        data.put("CO2", String.valueOf(400 + random.nextInt(100)));
        return data;
    }

    private void startAmqpConsumer() {
        amqpThread = new Thread(this::runAmqpLoop, "amqp-consumer");
        amqpThread.setDaemon(true);
        amqpThread.start();
    }

    private void runAmqpLoop() {
        long lastDbTime = 0;
        int retry = 0;
        while (running.get()) {
            try {
                AppProperties.Amqp cfg = appProperties.getAmqp();
                String url = "amqps://" + cfg.getHost() + ":" + cfg.getPort();
                String username = "accessKey=" + cfg.getAccessKey()
                        + "|timestamp=" + System.currentTimeMillis()
                        + "|instanceId=" + cfg.getInstanceId();

                JmsConnectionFactory factory = new JmsConnectionFactory(url);
                factory.setUsername(username);
                factory.setPassword(cfg.getAccessCode());

                amqpConnection = factory.createConnection();
                Session session = amqpConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Queue queue = session.createQueue(cfg.getQueue());
                MessageConsumer consumer = session.createConsumer(queue);
                amqpConnection.start();
                retry = 0;
                log.info("AMQP 连接成功: {}", url);

                while (running.get()) {
                    Message message = consumer.receive(1000);
                    if (message instanceof TextMessage textMessage) {
                        Map<String, String> data = parseAmqpMessage(textMessage.getText(), cfg.getDeviceId());
                        if (!data.isEmpty()) {
                            long now = System.currentTimeMillis();
                            updateLatest(data);
                            if (now - lastDbTime >= appProperties.getAmqpDbInterval() * 1000L) {
                                persistData(data, true);
                                lastDbTime = now;
                                log.info("AMQP 入库: Temp={}, Humi={}", data.get("Temp"), data.get("Humi"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                if (running.get()) {
                    retry++;
                    int wait = Math.min(retry * 5, 60);
                    log.error("AMQP 连接异常 (第{}次): {}", retry, e.getMessage());
                    sleepSeconds(wait);
                }
            }
        }
    }

    private Map<String, String> parseAmqpMessage(String body, String defaultDeviceId) {
        try {
            JsonNode root = objectMapper.readTree(body);
            String deviceId = root.path("device_id").asText(defaultDeviceId);
            if (deviceId.contains("_")) {
                deviceId = deviceId.substring(deviceId.lastIndexOf('_') + 1);
            }
            JsonNode notify = root.path("notify_data").path("body");
            if (notify.isTextual()) {
                notify = objectMapper.readTree(notify.asText());
            }
            Map<String, String> props = extractProperties(notify);
            props.put("device_id", deviceId);
            return props;
        } catch (Exception e) {
            log.warn("AMQP 消息解析失败: {}", e.getMessage());
            return Map.of();
        }
    }

    private Map<String, String> extractProperties(JsonNode node) {
        Map<String, String> result = new HashMap<>();
        if (node.has("services") && node.get("services").isArray()) {
            for (JsonNode svc : node.get("services")) {
                JsonNode props = svc.path("properties");
                Iterator<String> fields = props.fieldNames();
                while (fields.hasNext()) {
                    String key = fields.next();
                    result.put(key, props.get(key).asText());
                }
            }
            return result;
        }
        Iterator<String> fields = node.fieldNames();
        while (fields.hasNext()) {
            String key = fields.next();
            result.put(key, node.get(key).asText());
        }
        return result;
    }

    private void persistData(Map<String, String> data, boolean markOnline) {
        if (markOnline) {
            deviceService.markOnline(data.get("device_id"));
        }
        Long id = sensorDataService.insert(data);
        SensorData latest = sensorDataService.queryLatestOne(data.get("device_id"));
        if (latest == null) {
            latest = new SensorData();
            latest.setId(id);
            latest.setDeviceId(data.get("device_id"));
            latest.setTemp(data.get("Temp"));
            latest.setHumi(data.get("Humi"));
            latest.setLumi(data.get("Lumi"));
            latest.setLampST(data.get("LampST"));
            latest.setCondST(data.get("CondST"));
            latest.setVentST(data.get("VentST"));
            latest.setBuzzerST(data.get("BuzzerST"));
            latest.setSmoke(data.get("Smoke"));
            latest.setCo2(data.get("CO2"));
        }
        updateLatestEntity(latest);
    }

    private void updateLatest(Map<String, String> data) {
        SensorData latest = new SensorData();
        latest.setDeviceId(data.get("device_id"));
        latest.setTemp(data.get("Temp"));
        latest.setHumi(data.get("Humi"));
        latest.setLumi(data.get("Lumi"));
        latest.setLampST(data.get("LampST"));
        latest.setCondST(data.get("CondST"));
        latest.setVentST(data.get("VentST"));
        latest.setBuzzerST(data.get("BuzzerST"));
        latest.setSmoke(data.get("Smoke"));
        latest.setCo2(data.get("CO2"));
        updateLatestEntity(latest);
    }

    private void updateLatestEntity(SensorData latest) {
        latestDataHolder.update(latest);
        webSocketHandler.broadcast(Map.of("type", "sensor_update", "data", latest));
    }

    private void sleepSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
