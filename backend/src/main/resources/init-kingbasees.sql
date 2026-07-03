-- ============================================================
-- KingbaseES 初始化脚本
-- 每次启动时清空并重建三张业务表，插入默认设备 roomone
-- 注意：users 表由 AuthService 代码管理（CREATE TABLE IF NOT EXISTS），此处不涉及
-- ============================================================

-- 先删除有外键依赖的表（顺序：先子表后父表）
DROP TABLE IF EXISTS device_commands;
DROP TABLE IF EXISTS sensor_data;
DROP TABLE IF EXISTS device_info;

-- ============================================================
-- device_info 设备信息表
-- ============================================================
CREATE TABLE device_info (
    id          SERIAL PRIMARY KEY,
    device_id   VARCHAR(100) NOT NULL UNIQUE,
    device_name VARCHAR(200) NOT NULL DEFAULT '',
    device_type VARCHAR(50)  NOT NULL DEFAULT 'sensor',
    location    VARCHAR(200) NOT NULL DEFAULT '',
    status      VARCHAR(50)  NOT NULL DEFAULT 'offline',
    remarks     TEXT         NOT NULL DEFAULT '',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- sensor_data 传感器数据表
-- ============================================================
CREATE TABLE sensor_data (
    id          SERIAL PRIMARY KEY,
    device_id   VARCHAR(100) NOT NULL DEFAULT 'roomone',
    Temp        VARCHAR(50)  NOT NULL DEFAULT '',
    Humi        VARCHAR(50)  NOT NULL DEFAULT '',
    Lumi        VARCHAR(50)  NOT NULL DEFAULT '',
    LampST      VARCHAR(20)  NOT NULL DEFAULT 'OFF',
    CondST      VARCHAR(20)  NOT NULL DEFAULT 'OFF',
    VentST      VARCHAR(20)  NOT NULL DEFAULT 'OFF',
    BuzzerST    VARCHAR(20)  NOT NULL DEFAULT 'OFF',
    Smoke       VARCHAR(50)  NOT NULL DEFAULT '0',
    CO2         VARCHAR(50)  NOT NULL DEFAULT '400',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sensor_device FOREIGN KEY (device_id)
        REFERENCES device_info(device_id)
);

CREATE INDEX idx_sensor_data_device ON sensor_data (device_id, create_time DESC);

-- ============================================================
-- device_commands 命令记录表
-- ============================================================
CREATE TABLE device_commands (
    id          SERIAL PRIMARY KEY,
    device_id   VARCHAR(100) NOT NULL DEFAULT 'roomone',
    command     VARCHAR(100) NOT NULL,
    param_key   VARCHAR(100) NOT NULL DEFAULT '',
    param_value VARCHAR(100) NOT NULL DEFAULT '',
    status      VARCHAR(50)  NOT NULL DEFAULT 'pending',
    create_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_command_device FOREIGN KEY (device_id)
        REFERENCES device_info(device_id)
);

CREATE INDEX idx_commands_device ON device_commands (device_id, create_time DESC);

-- ============================================================
-- 插入默认设备 roomone（外键依赖的父记录）
-- ============================================================
INSERT INTO device_info (device_id, device_name, device_type, location, status, remarks)
VALUES ('roomone', '1号蘑菇大棚', 'sensor', '沈阳网联实验室', 'offline', 'WS63端侧默认设备');
