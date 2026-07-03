package com.smartmushroom.controller;

import com.smartmushroom.dto.ApiResponse;
import com.smartmushroom.entity.User;
import com.smartmushroom.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "用户认证")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户注册
     *
     * @param params 请求体：{ username, password, nickname }
     * @return 注册结果
     */
    @PostMapping("/register")
    public ApiResponse<User> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String nickname = params.get("nickname");

        // 参数校验
        if (username == null || username.isBlank()) {
            return ApiResponse.fail("用户名不能为空");
        }
        if (password == null || password.isBlank()) {
            return ApiResponse.fail("密码不能为空");
        }
        if (password.length() < 4) {
            return ApiResponse.fail("密码长度不能少于4位");
        }

        User user = authService.register(username.trim(), password, nickname);
        if (user == null) {
            return ApiResponse.fail("注册失败，用户名可能已存在");
        }

        user.setPassword(null);
        log.info("注册成功：username={}", username);
        return ApiResponse.ok(user);
    }

    /**
     * 用户登录
     *
     * @param params 请求体：{ username, password }
     * @return 登录结果，包含用户信息和token
     */
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");

        if (username == null || username.isBlank()) {
            return ApiResponse.fail("用户名不能为空");
        }
        if (password == null || password.isBlank()) {
            return ApiResponse.fail("密码不能为空");
        }

        User user = authService.login(username.trim(), password);
        if (user == null) {
            return ApiResponse.fail("用户名或密码错误");
        }

        // 生成token
        String token = authService.generateToken(user);

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("token", token);

        log.info("登录成功：username={}", username);
        return ApiResponse.ok(result);
    }

    /**
     * 获取当前用户信息（需要token）
     *
     * @param token 请求头 Authorization: Bearer {token}
     * @return 当前用户信息
     */
    @GetMapping("/profile")
    public ApiResponse<User> profile(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) {
            return ApiResponse.fail("未提供有效的认证token");
        }

        User user = authService.getCurrentUser(token);
        if (user == null) {
            return ApiResponse.fail("token无效或已过期");
        }

        return ApiResponse.ok(user);
    }

    /**
     * 退出登录
     *
     * @param authHeader 请求头 Authorization: Bearer {token}
     * @return 退出结果
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        if (token != null) {
            authService.logout(token);
        }
        return ApiResponse.ok(null);
    }

    /**
     * 从Authorization请求头中提取token（去掉 "Bearer " 前缀）
     */
    private String extractToken(String authHeader) {
        if (authHeader == null || authHeader.isBlank()) {
            return null;
        }
        if (authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7).trim();
        }
        return authHeader.trim();
    }
}
