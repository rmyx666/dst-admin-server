CREATE TABLE IF NOT EXISTS player_log (
    id INTEGER PRIMARY KEY,              -- 主键ID
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,  -- 创建时间，默认值为当前时间
    room_id TEXT,                        -- 房间ID
    user_id TEXT,                        -- 用户ID
    name TEXT,                           -- 玩家昵称
    prefab TEXT,                         -- 玩家角色
    playerage INTEGER                    -- 玩家生存天数
);
