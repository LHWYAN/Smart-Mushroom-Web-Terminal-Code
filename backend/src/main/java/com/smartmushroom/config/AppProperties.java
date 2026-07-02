package com.smartmushroom.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String dbType = "sqlite";
    private String dataSource = "simulate";
    private String sqlitePath = "./sensor_data.db";
    private int amqpDbInterval = 30;
    private int simulateInterval = 30;
    private Kingbase kingbase = new Kingbase();
    private Amqp amqp = new Amqp();
    private Dify dify = new Dify();

    @Data
    public static class Kingbase {
        private String host = "127.0.0.1";
        private int port = 54321;
        private String database = "smart_room";
        private String username = "system";
        private String password = "123456";
    }

    @Data
    public static class Amqp {
        private String host;
        private int port = 5671;
        private String queue;
        private String accessKey;
        private String accessCode;
        private String instanceId;
        private String deviceId;
    }

    @Data
    public static class Dify {
        private String apiUrl = "http://localhost/v1/chat-messages";
        private String apiKey;
    }
}
