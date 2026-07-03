package com.smartmushroom.controller;

import com.smartmushroom.mapper.DeviceCommandMapper;
import com.smartmushroom.mapper.DeviceInfoMapper;
import com.smartmushroom.mapper.SensorDataMapper;
import com.smartmushroom.service.AiService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link AiController} 切片测试。
 * 重点验证 @NotBlank 参数校验、正常对话、异常处理。
 */
@DisplayName("AiController - AI 助手接口")
@WebMvcTest(AiController.class)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AiService aiService;
    @MockBean
    private DeviceInfoMapper deviceInfoMapper;
    @MockBean
    private SensorDataMapper sensorDataMapper;
    @MockBean
    private DeviceCommandMapper deviceCommandMapper;

    @Test
    @DisplayName("POST /ai/chat 正常提问返回 code=0")
    void chat_normalRequest_returnsOk() throws Exception {
        when(aiService.chat(anyString())).thenReturn(Map.of("answer", "蘑菇适宜温度18-25℃。"));

        mockMvc.perform(post("/api/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"蘑菇适宜温度是多少？\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.answer").value("蘑菇适宜温度18-25℃。"));
    }

    @Test
    @DisplayName("POST /ai/chat 空问题（@NotBlank 校验）返回 400")
    void chat_emptyQuestion_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"\"}"))
                .andExpect(status().isBadRequest());

        verify(aiService, never()).chat(anyString());
    }

    @Test
    @DisplayName("POST /ai/chat 缺少 question 字段返回 400")
    void chat_missingQuestion_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(aiService, never()).chat(anyString());
    }

    @Test
    @DisplayName("POST /ai/chat AiService 抛异常时返回 code=-1")
    void chat_serviceThrows_returnsFail() throws Exception {
        when(aiService.chat(anyString())).thenThrow(new RuntimeException("Dify 服务不可用"));

        mockMvc.perform(post("/api/v1/ai/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"question\":\"测试\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("Dify 服务不可用"));
    }
}
