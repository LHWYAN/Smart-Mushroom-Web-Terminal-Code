package com.smartmushroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmushroom.entity.User;
import com.smartmushroom.mapper.DeviceCommandMapper;
import com.smartmushroom.mapper.DeviceInfoMapper;
import com.smartmushroom.mapper.SensorDataMapper;
import com.smartmushroom.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * {@link AuthController} 切片测试。
 * 覆盖注册、登录、profile、logout 的正常/异常/边界场景。
 */
@DisplayName("AuthController - 用户认证接口")
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;
    @MockBean
    private DeviceInfoMapper deviceInfoMapper;
    @MockBean
    private SensorDataMapper sensorDataMapper;
    @MockBean
    private DeviceCommandMapper deviceCommandMapper;

    private User sampleUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setNickname("Alice");
        user.setRole("user");
        user.setStatus("active");
        return user;
    }

    // ==================== POST /auth/register ====================

    @Test
    @DisplayName("POST /auth/register 注册成功返回用户信息（无密码）")
    void register_success_returnsUser() throws Exception {
        User user = sampleUser();
        when(authService.register(eq("alice"), eq("pass1234"), eq("Alice"))).thenReturn(user);

        Map<String, String> body = Map.of(
                "username", "alice",
                "password", "pass1234",
                "nickname", "Alice"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    @DisplayName("POST /auth/register 用户名为空返回 fail")
    void register_blankUsername_returnsFail() throws Exception {
        Map<String, String> body = Map.of("username", "  ", "password", "pass1234");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("用户名不能为空"));

        verify(authService, never()).register(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("POST /auth/register 密码为空返回 fail")
    void register_blankPassword_returnsFail() throws Exception {
        Map<String, String> body = Map.of("username", "alice", "password", "  ");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("密码不能为空"));
    }

    @Test
    @DisplayName("POST /auth/register 密码少于4位返回 fail")
    void register_shortPassword_returnsFail() throws Exception {
        Map<String, String> body = Map.of("username", "alice", "password", "123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("密码长度不能少于4位"));
    }

    @Test
    @DisplayName("POST /auth/register 用户名已存在返回 fail")
    void register_duplicateUsername_returnsFail() throws Exception {
        when(authService.register(eq("alice"), eq("pass1234"), isNull())).thenReturn(null);

        Map<String, String> body = Map.of("username", " alice ", "password", "pass1234");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("注册失败，用户名可能已存在"));

        verify(authService).register("alice", "pass1234", null);
    }

    // ==================== POST /auth/login ====================

    @Test
    @DisplayName("POST /auth/login 登录成功返回 user 与 token")
    void login_success_returnsUserAndToken() throws Exception {
        User user = sampleUser();
        when(authService.login("alice", "pass1234")).thenReturn(user);
        when(authService.generateToken(user)).thenReturn("mock-token-abc");

        Map<String, String> body = Map.of("username", " alice ", "password", "pass1234");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.user.username").value("alice"))
                .andExpect(jsonPath("$.data.token").value("mock-token-abc"));
    }

    @Test
    @DisplayName("POST /auth/login 用户名为空返回 fail")
    void login_blankUsername_returnsFail() throws Exception {
        Map<String, String> body = Map.of("username", "", "password", "pass1234");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("用户名不能为空"));
    }

    @Test
    @DisplayName("POST /auth/login 密码为空返回 fail")
    void login_blankPassword_returnsFail() throws Exception {
        Map<String, String> body = Map.of("username", "alice", "password", "");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("密码不能为空"));
    }

    @Test
    @DisplayName("POST /auth/login 认证失败返回 fail")
    void login_invalidCredentials_returnsFail() throws Exception {
        when(authService.login("alice", "wrong")).thenReturn(null);

        Map<String, String> body = Map.of("username", "alice", "password", "wrong");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    // ==================== GET /auth/profile ====================

    @Test
    @DisplayName("GET /auth/profile Bearer token 有效时返回用户信息")
    void profile_validBearerToken_returnsUser() throws Exception {
        User user = sampleUser();
        when(authService.getCurrentUser("valid-token")).thenReturn(user);

        mockMvc.perform(get("/api/v1/auth/profile")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    @DisplayName("GET /auth/profile 无 Authorization 返回 fail")
    void profile_missingHeader_returnsFail() throws Exception {
        mockMvc.perform(get("/api/v1/auth/profile"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /auth/profile 空 Bearer token 返回 fail")
    void profile_blankBearerToken_returnsFail() throws Exception {
        mockMvc.perform(get("/api/v1/auth/profile")
                        .header("Authorization", "Bearer   "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("token无效或已过期"));
    }

    @Test
    @DisplayName("GET /auth/profile 裸 token（无 Bearer 前缀）也可解析")
    void profile_rawToken_returnsUser() throws Exception {
        User user = sampleUser();
        when(authService.getCurrentUser("raw-token")).thenReturn(user);

        mockMvc.perform(get("/api/v1/auth/profile")
                        .header("Authorization", "raw-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.username").value("alice"));
    }

    @Test
    @DisplayName("GET /auth/profile token 无效返回 fail")
    void profile_invalidToken_returnsFail() throws Exception {
        when(authService.getCurrentUser("bad-token")).thenReturn(null);

        mockMvc.perform(get("/api/v1/auth/profile")
                        .header("Authorization", "Bearer bad-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(-1))
                .andExpect(jsonPath("$.message").value("token无效或已过期"));
    }

    // ==================== POST /auth/logout ====================

    @Test
    @DisplayName("POST /auth/logout 有效 token 时调用 logout")
    void logout_withToken_callsService() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(authService).logout("valid-token");
    }

    @Test
    @DisplayName("POST /auth/logout 无 token 仍返回成功")
    void logout_noToken_stillOk() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header("Authorization", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0));

        verify(authService, never()).logout(anyString());
    }
}
