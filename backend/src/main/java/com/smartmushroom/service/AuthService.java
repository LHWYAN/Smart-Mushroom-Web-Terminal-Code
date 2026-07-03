package com.smartmushroom.service;

import com.smartmushroom.entity.User;
import com.smartmushroom.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final JdbcTemplate kingbaseJdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(JdbcTemplate kingbaseJdbcTemplate) {
        this.kingbaseJdbcTemplate = kingbaseJdbcTemplate;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // ==================== 表初始化 ====================

    @PostConstruct
    public void initTable() {
        log.info("正在初始化 KingbaseES users 表...");
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id          SERIAL PRIMARY KEY,
                    username    VARCHAR(100) NOT NULL UNIQUE,
                    password    VARCHAR(255) NOT NULL,
                    nickname    VARCHAR(100) DEFAULT '',
                    role        VARCHAR(20) DEFAULT 'user',
                    status      VARCHAR(20) DEFAULT 'active',
                    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
        kingbaseJdbcTemplate.execute(sql);
        log.info("users 表初始化完成");

        // 初始化默认管理员账户：admin / admin123
        initDefaultAdmin();
    }

    private void initDefaultAdmin() {
        String countSql = "SELECT COUNT(*) FROM users WHERE username = ?";
        Integer count = kingbaseJdbcTemplate.queryForObject(countSql, Integer.class, "admin");
        if (count == null || count == 0) {
            String encodedPwd = passwordEncoder.encode("admin123");
            String insertSql = """
                    INSERT INTO users (username, password, nickname, role, status)
                    VALUES (?, ?, ?, 'admin', 'active')
                    """;
            kingbaseJdbcTemplate.update(insertSql, "admin", encodedPwd, "管理员");
            log.info("默认管理员账户已创建（admin/admin123）");
        } else {
            log.info("管理员账户已存在，跳过初始化");
        }
    }

    // ==================== 用户映射 ====================

    private final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setNickname(rs.getString("nickname"));
            user.setRole(rs.getString("role"));
            user.setStatus(rs.getString("status"));
            user.setCreateTime(rs.getObject("create_time") != null ? rs.getObject("create_time").toString() : null);
            user.setUpdateTime(rs.getObject("update_time") != null ? rs.getObject("update_time").toString() : null);
            return user;
        }
    };

    // ==================== 注册 ====================

    /**
     * 注册新用户
     *
     * @param username 用户名
     * @param password 明文密码
     * @param nickname 昵称
     * @return 注册成功返回User对象，失败返回null
     */
    public User register(String username, String password, String nickname) {
        // 检查用户名是否已存在
        User existing = getUserByUsername(username);
        if (existing != null) {
            log.warn("注册失败：用户名 {} 已存在", username);
            return null;
        }

        String encodedPwd = passwordEncoder.encode(password);
        String nick = (nickname == null || nickname.isBlank()) ? username : nickname;

        String sql = """
                INSERT INTO users (username, password, nickname, role, status)
                VALUES (?, ?, ?, 'user', 'active')
                """;
        kingbaseJdbcTemplate.update(sql, username, encodedPwd, nick);

        log.info("用户注册成功：username={}, nickname={}", username, nick);
        return getUserByUsername(username);
    }

    // ==================== 登录 ====================

    /**
     * 用户登录验证
     *
     * @param username 用户名
     * @param password 明文密码
     * @return 登录成功返回User对象（无密码），失败返回null
     */
    public User login(String username, String password) {
        User user = getUserByUsername(username);
        if (user == null) {
            log.warn("登录失败：用户 {} 不存在", username);
            return null;
        }

        // 检查账户状态
        if (!"active".equalsIgnoreCase(user.getStatus())) {
            log.warn("登录失败：用户 {} 已被禁用", username);
            return null;
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("登录失败：用户 {} 密码错误", username);
            return null;
        }

        // 移除密码后返回
        user.setPassword(null);
        log.info("用户登录成功：username={}", username);
        return user;
    }

    // ==================== 查询用户 ====================

    /**
     * 根据用户名获取用户（含密码hash）
     */
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        List<User> list = kingbaseJdbcTemplate.query(sql, userRowMapper, username);
        return list.isEmpty() ? null : list.get(0);
    }

    // ==================== 生成Token ====================

    /**
     * 为用户生成登录token
     */
    public String generateToken(User user) {
        return JwtUtil.generateToken(user);
    }

    /**
     * 根据token获取当前登录用户（不含密码）
     */
    public User getCurrentUser(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        String username = JwtUtil.validateToken(token);
        if (username == null) {
            return null;
        }
        User user = getUserByUsername(username);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    /**
     * 退出登录，移除token
     */
    public void logout(String token) {
        JwtUtil.removeToken(token);
    }
}
