package com.smartmushroom.service;

import com.smartmushroom.entity.User;
import com.smartmushroom.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * {@link AuthService} 单元测试。
 * Mock JdbcTemplate，覆盖注册、登录、Token 与当前用户查询。
 */
@DisplayName("AuthService - 用户认证")
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JdbcTemplate kingbaseJdbcTemplate;

    @InjectMocks
    private AuthService authService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @BeforeEach
    void clearJwtStore() throws Exception {
        for (String fieldName : List.of("TOKEN_STORE", "USER_TOKEN_MAP")) {
            Field field = JwtUtil.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, ?> map = (Map<String, ?>) field.get(null);
            map.clear();
        }
    }

    private User storedUser(String username, String plainPassword, String status) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(encoder.encode(plainPassword));
        user.setNickname("昵称");
        user.setRole("user");
        user.setStatus(status);
        return user;
    }

    @SuppressWarnings("unchecked")
    private void mockQueryUser(String username, List<User> result) {
        when(kingbaseJdbcTemplate.query(contains("SELECT"), any(RowMapper.class), eq(username)))
                .thenReturn(result);
    }

    // ==================== 表初始化 ====================

    @Test
    @DisplayName("initTable() 创建 users 表并初始化默认管理员")
    void initTable_createsTableAndDefaultAdmin() {
        when(kingbaseJdbcTemplate.queryForObject(contains("COUNT"), eq(Integer.class), eq("admin")))
                .thenReturn(0);

        authService.initTable();

        verify(kingbaseJdbcTemplate).execute(contains("CREATE TABLE IF NOT EXISTS users"));
        verify(kingbaseJdbcTemplate).update(contains("INSERT INTO users"), eq("admin"), anyString(), eq("管理员"));
    }

    @Test
    @DisplayName("initTable() 管理员已存在时跳过创建")
    void initTable_adminExists_skipsInsert() {
        when(kingbaseJdbcTemplate.queryForObject(contains("COUNT"), eq(Integer.class), eq("admin")))
                .thenReturn(1);

        authService.initTable();

        verify(kingbaseJdbcTemplate).execute(contains("CREATE TABLE IF NOT EXISTS users"));
        verify(kingbaseJdbcTemplate, never()).update(contains("INSERT INTO users"), any(), any(), any());
    }

    @Test
    @DisplayName("getUserByUsername() RowMapper 正确映射字段")
    void getUserByUsername_rowMapper_mapsFields() throws Exception {
        ArgumentCaptor<RowMapper<User>> captor = ArgumentCaptor.forClass(RowMapper.class);
        when(kingbaseJdbcTemplate.query(contains("SELECT"), captor.capture(), eq("alice")))
                .thenReturn(Collections.emptyList());

        authService.getUserByUsername("alice");

        ResultSet rs = mock(ResultSet.class);
        when(rs.getLong("id")).thenReturn(2L);
        when(rs.getString("username")).thenReturn("alice");
        when(rs.getString("password")).thenReturn("hash");
        when(rs.getString("nickname")).thenReturn("Alice");
        when(rs.getString("role")).thenReturn("user");
        when(rs.getString("status")).thenReturn("active");
        when(rs.getObject("create_time")).thenReturn("2026-01-01");
        when(rs.getObject("update_time")).thenReturn("2026-01-02");

        User mapped = captor.getValue().mapRow(rs, 0);

        assertEquals(2L, mapped.getId());
        assertEquals("alice", mapped.getUsername());
        assertEquals("hash", mapped.getPassword());
        assertEquals("Alice", mapped.getNickname());
        assertEquals("user", mapped.getRole());
        assertEquals("active", mapped.getStatus());
        assertEquals("2026-01-01", mapped.getCreateTime());
        assertEquals("2026-01-02", mapped.getUpdateTime());
    }

    // ==================== register() ====================

    @Test
    @DisplayName("register() 新用户注册成功")
    void register_newUser_returnsUser() {
        User created = storedUser("alice", "pass1234", "active");
        created.setPassword("hashed");
        mockQueryUser("alice", Collections.emptyList(), List.of(created));

        User result = authService.register("alice", "pass1234", "Alice");

        assertNotNull(result);
        assertEquals("alice", result.getUsername());
        verify(kingbaseJdbcTemplate).update(contains("INSERT INTO users"), eq("alice"), anyString(), eq("Alice"));
    }

    @Test
    @DisplayName("register() nickname 为空时使用 username")
    void register_blankNickname_usesUsername() {
        User created = storedUser("bob", "pass1234", "active");
        mockQueryUser("bob", Collections.emptyList(), List.of(created));

        authService.register("bob", "pass1234", "  ");

        verify(kingbaseJdbcTemplate).update(contains("INSERT INTO users"), eq("bob"), anyString(), eq("bob"));
    }

    @Test
    @DisplayName("register() 用户名已存在返回 null")
    void register_duplicateUsername_returnsNull() {
        mockQueryUser("alice", List.of(storedUser("alice", "pass1234", "active")));

        assertNull(authService.register("alice", "pass1234", "Alice"));
        verify(kingbaseJdbcTemplate, never()).update(contains("INSERT INTO users"), any(), any(), any());
    }

    // ==================== login() ====================

    @Test
    @DisplayName("login() 密码正确返回无密码 User")
    void login_validCredentials_returnsUserWithoutPassword() {
        User stored = storedUser("alice", "pass1234", "active");
        mockQueryUser("alice", List.of(stored));

        User result = authService.login("alice", "pass1234");

        assertNotNull(result);
        assertEquals("alice", result.getUsername());
        assertNull(result.getPassword());
    }

    @Test
    @DisplayName("login() 用户不存在返回 null")
    void login_userNotFound_returnsNull() {
        mockQueryUser("ghost", Collections.emptyList());

        assertNull(authService.login("ghost", "pass1234"));
    }

    @Test
    @DisplayName("login() 密码错误返回 null")
    void login_wrongPassword_returnsNull() {
        mockQueryUser("alice", List.of(storedUser("alice", "pass1234", "active")));

        assertNull(authService.login("alice", "wrong-pass"));
    }

    @Test
    @DisplayName("login() 账户被禁用返回 null")
    void login_disabledAccount_returnsNull() {
        mockQueryUser("alice", List.of(storedUser("alice", "pass1234", "disabled")));

        assertNull(authService.login("alice", "pass1234"));
    }

    // ==================== Token / 当前用户 ====================

    @Test
    @DisplayName("generateToken() 返回非空 token")
    void generateToken_returnsToken() {
        User user = storedUser("alice", "pass1234", "active");
        user.setPassword(null);

        String token = authService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    @DisplayName("getCurrentUser() 有效 token 返回用户")
    void getCurrentUser_validToken_returnsUser() {
        User stored = storedUser("alice", "pass1234", "active");
        mockQueryUser("alice", List.of(stored));
        String token = authService.generateToken(stored);

        User result = authService.getCurrentUser(token);

        assertNotNull(result);
        assertEquals("alice", result.getUsername());
        assertNull(result.getPassword());
    }

    @Test
    @DisplayName("getCurrentUser() 无效 token 返回 null")
    void getCurrentUser_invalidToken_returnsNull() {
        assertNull(authService.getCurrentUser("invalid-token"));
    }

    @Test
    @DisplayName("getCurrentUser() token 为 blank 返回 null")
    void getCurrentUser_blankToken_returnsNull() {
        assertNull(authService.getCurrentUser("   "));
    }

    @Test
    @DisplayName("logout() 移除 token 后无法获取当前用户")
    void logout_removesToken() {
        User stored = storedUser("alice", "pass1234", "active");
        String token = authService.generateToken(stored);

        authService.logout(token);

        assertNull(authService.getCurrentUser(token));
    }

    @SuppressWarnings("unchecked")
    private void mockQueryUser(String username, List<User> first, List<User> second) {
        when(kingbaseJdbcTemplate.query(contains("SELECT"), any(RowMapper.class), eq(username)))
                .thenReturn(first)
                .thenReturn(second);
    }
}
