CREATE TABLE IF NOT EXISTS player_log (
    id INTEGER PRIMARY KEY,              -- 主键ID
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,  -- 创建时间，默认值为当前时间
    room_id TEXT,                        -- 房间ID
    user_id TEXT,                        -- 用户ID
    name TEXT,                           -- 玩家昵称
    prefab TEXT,                         -- 玩家角色
    playerage INTEGER                    -- 玩家生存天数
);
CREATE TABLE IF NOT EXISTS room_info (
    room_id VARCHAR(255) PRIMARY KEY,
    room_name VARCHAR(255),
    schedule_update_map TEXT,  -- JSON 存储
    schedule_backup_map TEXT,  -- JSON 存储
    not_start_master BOOLEAN,
    not_start_caves BOOLEAN,
    master_port VARCHAR(10),
    ground_port VARCHAR(10),
    caves_port VARCHAR(10)
);
CREATE TABLE IF NOT EXISTS server_info (
    id INTEGER PRIMARY KEY,                 -- 主键，自增ID
    ip VARCHAR(255) NOT NULL,              -- IP地址
    name VARCHAR(255) NOT NULL,            -- 名称
    username VARCHAR(255) NOT NULL,        -- 用户名
    password VARCHAR(255) NOT NULL         -- 密码
);
CREATE TABLE IF NOT EXISTS user (
    username VARCHAR(255) PRIMARY KEY,   -- 用户名作为主键
    password VARCHAR(255) NOT NULL,      -- 密码，不能为空
    nickname VARCHAR(255),               -- 昵称
    picture VARCHAR(255)                 -- 头像（图片URL）
);
