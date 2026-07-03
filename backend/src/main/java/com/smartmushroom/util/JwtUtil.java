package com.smartmushroom.util;

import com.smartmushroom.entity.User;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简易Token工具类（基于UUID + 内存存储）
 * 生产环境建议替换为标准的JWT实现（如jjwt库）
 */
public class JwtUtil {

    /** Token过期时间（毫秒）：24小时 */
    private static final long TOKEN_EXPIRE_MS = 24 * 60 * 60 * 1000L;

    /** token -> username 映射 */
    private static final Map<String, TokenEntry> TOKEN_STORE = new ConcurrentHashMap<>();

    /** username -> token 映射，便于快速查找和踢下线 */
    private static final Map<String, String> USER_TOKEN_MAP = new ConcurrentHashMap<>();

    private static class TokenEntry {
        final String username;
        final long expireAt;

        TokenEntry(String username, long expireAt) {
            this.username = username;
            this.expireAt = expireAt;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expireAt;
        }
    }

    /**
     * 为用户生成token
     */
    public static String generateToken(User user) {
        // 移除旧token
        String oldToken = USER_TOKEN_MAP.remove(user.getUsername());
        if (oldToken != null) {
            TOKEN_STORE.remove(oldToken);
        }

        // 生成新token
        String token = UUID.randomUUID().toString().replace("-", "");
        long expireAt = System.currentTimeMillis() + TOKEN_EXPIRE_MS;
        TOKEN_STORE.put(token, new TokenEntry(user.getUsername(), expireAt));
        USER_TOKEN_MAP.put(user.getUsername(), token);
        return token;
    }

    /**
     * 验证token，返回关联的用户名；无效或过期返回null
     */
    public static String validateToken(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        TokenEntry entry = TOKEN_STORE.get(token);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            TOKEN_STORE.remove(token);
            USER_TOKEN_MAP.remove(entry.username);
            return null;
        }
        return entry.username;
    }

    /**
     * 移除token（退出登录）
     */
    public static void removeToken(String token) {
        TokenEntry entry = TOKEN_STORE.remove(token);
        if (entry != null) {
            USER_TOKEN_MAP.remove(entry.username);
        }
    }

    /**
     * 根据用户名移除token（管理员强制下线）
     */
    public static void removeTokenByUsername(String username) {
        String token = USER_TOKEN_MAP.remove(username);
        if (token != null) {
            TOKEN_STORE.remove(token);
        }
    }
}
