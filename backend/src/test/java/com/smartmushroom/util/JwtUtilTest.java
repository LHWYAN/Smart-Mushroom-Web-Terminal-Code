package com.smartmushroom.util;

import com.smartmushroom.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@link JwtUtil} 单元测试。
 * 覆盖 token 生成、验证、过期、移除及单用户 token 替换。
 */
@DisplayName("JwtUtil - Token 工具类")
class JwtUtilTest {

    @BeforeEach
    void clearTokenStore() throws Exception {
        clearMap("TOKEN_STORE");
        clearMap("USER_TOKEN_MAP");
    }

    private void clearMap(String fieldName) throws Exception {
        Field field = JwtUtil.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, ?> map = (Map<String, ?>) field.get(null);
        map.clear();
    }

    private User user(String username) {
        User user = new User();
        user.setUsername(username);
        user.setId(1L);
        return user;
    }

    // ==================== 正常用例 ====================

    @Test
    @DisplayName("generateToken 后 validateToken 返回正确用户名")
    void generateThenValidate_returnsUsername() {
        String token = JwtUtil.generateToken(user("alice"));

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals("alice", JwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("同一用户重复登录时旧 token 失效")
    void regenerateToken_invalidatesOldToken() {
        String oldToken = JwtUtil.generateToken(user("bob"));
        String newToken = JwtUtil.generateToken(user("bob"));

        assertNull(JwtUtil.validateToken(oldToken));
        assertEquals("bob", JwtUtil.validateToken(newToken));
    }

    @Test
    @DisplayName("removeToken 后 token 无法验证")
    void removeToken_invalidatesToken() {
        String token = JwtUtil.generateToken(user("carol"));
        JwtUtil.removeToken(token);

        assertNull(JwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("removeTokenByUsername 移除该用户全部 token")
    void removeTokenByUsername_invalidatesToken() {
        String token = JwtUtil.generateToken(user("dave"));
        JwtUtil.removeTokenByUsername("dave");

        assertNull(JwtUtil.validateToken(token));
    }

    // ==================== 异常用例 ====================

    @Test
    @DisplayName("validateToken(null) 返回 null")
    void validateToken_null_returnsNull() {
        assertNull(JwtUtil.validateToken(null));
    }

    @Test
    @DisplayName("validateToken(空白) 返回 null")
    void validateToken_blank_returnsNull() {
        assertNull(JwtUtil.validateToken("   "));
    }

    @Test
    @DisplayName("validateToken(不存在的 token) 返回 null")
    void validateToken_unknown_returnsNull() {
        assertNull(JwtUtil.validateToken("not-a-real-token"));
    }

    // ==================== 边界用例 ====================

    @Test
    @DisplayName("token 过期后 validateToken 返回 null 并清理存储")
    void validateToken_expired_returnsNull() throws Exception {
        String token = JwtUtil.generateToken(user("eve"));

        Field tokenStoreField = JwtUtil.class.getDeclaredField("TOKEN_STORE");
        tokenStoreField.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Object> tokenStore = (Map<String, Object>) tokenStoreField.get(null);

        Object entry = tokenStore.get(token);
        Field expireAtField = entry.getClass().getDeclaredField("expireAt");
        expireAtField.setAccessible(true);
        expireAtField.set(entry, System.currentTimeMillis() - 1);

        assertNull(JwtUtil.validateToken(token));
        assertNull(JwtUtil.validateToken(token));
    }

    @Test
    @DisplayName("removeToken(不存在的 token) 不抛异常")
    void removeToken_unknown_noException() {
        assertDoesNotThrow(() -> JwtUtil.removeToken("ghost-token"));
    }

    @Test
    @DisplayName("removeTokenByUsername(无 token 用户) 不抛异常")
    void removeTokenByUsername_noToken_noException() {
        assertDoesNotThrow(() -> JwtUtil.removeTokenByUsername("nobody"));
    }
}
