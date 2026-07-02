package com.smartmushroom.controller;

import com.smartmushroom.dto.AiChatRequest;
import com.smartmushroom.dto.ApiResponse;
import com.smartmushroom.service.AiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@Tag(name = "AI 助手")
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @Operation(summary = "AI 对话")
    @PostMapping("/chat")
    public ApiResponse<Map<String, Object>> chat(@Valid @RequestBody AiChatRequest request) {
        try {
            return ApiResponse.ok(aiService.chat(request.getQuestion()));
        } catch (Exception e) {
            log.error("AI 助手调用失败: {}", e.getMessage());
            return ApiResponse.fail(e.getMessage());
        }
    }
}
