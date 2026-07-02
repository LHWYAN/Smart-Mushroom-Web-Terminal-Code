CREATE TABLE IF NOT EXISTS device_info (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id   TEXT NOT NULL UNIQUE,
    device_name TEXT NOT NULL DEFAULT '',
    device_type TEXT NOT NULL DEFAULT 'sensor',
    location    TEXT NOT NULL DEFAULT '',
    status      TEXT NOT NULL DEFAULT 'offline',
    remarks     TEXT NOT NULL DEFAULT '',
    create_time TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    update_time TEXT NOT NULL DEFAULT (datetime('now', 'localtime'))
);

CREATE TABLE IF NOT EXISTS sensor_data (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id   TEXT NOT NULL DEFAULT 'roomone',
    Temp        TEXT NOT NULL DEFAULT '',
    Humi        TEXT NOT NULL DEFAULT '',
    Lumi        TEXT NOT NULL DEFAULT '',
    LampST      TEXT NOT NULL DEFAULT 'OFF',
    CondST      TEXT NOT NULL DEFAULT 'OFF',
    VentST      TEXT NOT NULL DEFAULT 'OFF',
    BuzzerST    TEXT NOT NULL DEFAULT 'OFF',
    Smoke       TEXT NOT NULL DEFAULT '0',
    CO2         TEXT NOT NULL DEFAULT '400',
    create_time TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (device_id) REFERENCES device_info(device_id)
);

CREATE INDEX IF NOT EXISTS idx_sensor_data_device ON sensor_data (device_id, create_time DESC);

CREATE TABLE IF NOT EXISTS device_commands (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    device_id   TEXT NOT NULL DEFAULT 'roomone',
    command     TEXT NOT NULL,
    param_key   TEXT NOT NULL DEFAULT '',
    param_value TEXT NOT NULL DEFAULT '',
    status      TEXT NOT NULL DEFAULT 'pending',
    create_time TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    update_time TEXT NOT NULL DEFAULT (datetime('now', 'localtime')),
    FOREIGN KEY (device_id) REFERENCES device_info(device_id)
);

CREATE INDEX IF NOT EXISTS idx_commands_device ON device_commands (device_id, create_time DESC);

INSERT OR IGNORE INTO device_info (device_id, device_name, device_type, location, status)
VALUES ('roomone', '一号房间', 'sensor', '沈阳网联实验室', 'offline');
