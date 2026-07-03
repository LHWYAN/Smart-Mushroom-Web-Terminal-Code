package com.smartmushroom.service;

import com.smartmushroom.config.AppProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * {@link AiService} 单元测试。
 * Mock AppProperties 和 RestTemplate（通过 ReflectionTestUtils 替换内部字段），
 * 验证 Dify API 请求构造、返回结果经 AiAnswerSanitizer 处理、异常场景。
 */
@DisplayName("AiService - AI 对话服务")
@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private AppProperties appProperties;
    @Mock
    private RestTemplate restTemplate;

    private AiService aiService;

    @BeforeEach
    void setUp() {
        // AppProperties.Dify mock
        AppProperties.Dify dify = new AppProperties.Dify();
        dify.setApiUrl("http://mock-dify/v1/chat-messages");
        dify.setApiKey("test-api-key");
        lenient().when(appProperties.getDify()).thenReturn(dify);

        // 创建 AiService 实例并注入 mock 的 appProperties
        aiService = new AiService(appProperties);
        // 用反射替换内部 new RestTemplate() 为 mock 对象
        ReflectionTestUtils.setField(aiService, "restTemplate", restTemplate);
    }

    @Test
    @DisplayName("chat() 正常请求：返回经 sanitize 处理后的 answer")
    void chat_normalRequest_returnsSanitizedAnswer() {
        Map<String, Object> mockResponse = Map.of("answer", "<think>思考链</think>蘑菇适宜温度18-25℃。");
        when(restTemplate.postForObject(eq("http://mock-dify/v1/chat-messages"), any(), eq(Map.class)))
                .thenReturn(mockResponse);

        Map<String, Object> result = aiService.chat("蘑菇适宜温度是多少？");

        assertNotNull(result);
        assertEquals("蘑菇适宜温度18-25℃。", result.get("answer"), "应剥离 think 标签");
    }

    @Test
    @DisplayName("chat() 返回的 answer 为纯文本时原样返回")
    void chat_plainAnswer_returnedAsIs() {
        Map<String, Object> mockResponse = Map.of("answer", "湿度应保持在80%-90%。");
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(mockResponse);

        Map<String, Object> result = aiService.chat("湿度要求？");

        assertEquals("湿度应保持在80%-90%。", result.get("answer"));
    }

    @Test
    @DisplayName("chat() Dify 返回 null 时 answer 为空字符串")
    void chat_nullResponse_returnsEmptyAnswer() {
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(null);

        Map<String, Object> result = aiService.chat("测试");

        assertEquals("", result.get("answer"), "null 响应经 sanitize 后应为空字符串");
    }

    @Test
    @DisplayName("chat() Dify 返回的 answer 为 null 时，sanitize 后为空字符串")
    void chat_nullAnswer_returnsEmptyAnswer() {
        Map<String, Object> mockResponse = new java.util.HashMap<>();
        mockResponse.put("answer", null);
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class))).thenReturn(mockResponse);

        Map<String, Object> result = aiService.chat("测试");

        assertEquals("", result.get("answer"));
    }

    @Test
    @DisplayName("chat() RestTemplate 抛出异常时异常向上传播")
    void chat_restTemplateThrows_propagatesException() {
        when(restTemplate.postForObject(anyString(), any(), eq(Map.class)))
                .thenThrow(new RuntimeException("Dify 服务不可用"));

        assertThrows(RuntimeException.class, () -> aiService.chat("测试"));
    }

    @Test
    @DisplayName("chat() 验证请求发送到正确的 API URL")
    void chat_correctApiUrl_called() {
        Map<String, Object> mockResponse = Map.of("answer", "回答");
        when(restTemplate.postForObject(eq("http://mock-dify/v1/chat-messages"), any(), eq(Map.class)))
                .thenReturn(mockResponse);

        aiService.chat("问题");

        verify(restTemplate).postForObject(eq("http://mock-dify/v1/chat-messages"), any(), eq(Map.class));
    }
}
