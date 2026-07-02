package com.smartmushroom.service;

import com.smartmushroom.config.AppProperties;
import com.smartmushroom.entity.SensorData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AppProperties appProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> chat(String question) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(appProperties.getDify().getApiKey());

        Map<String, Object> payload = new HashMap<>();
        payload.put("inputs", Map.of());
        payload.put("query", question);
        payload.put("response_mode", "blocking");
        payload.put("conversation_id", "");
        payload.put("user", "smart-mushroom-web");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(
                appProperties.getDify().getApiUrl(), entity, Map.class);

        Map<String, Object> result = new HashMap<>();
        result.put("answer", response != null ? response.getOrDefault("answer", "") : "");
        result.put("raw", response);
        return result;
    }
}
