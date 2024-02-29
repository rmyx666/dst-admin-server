package com.tugos.dst.admin.utils;

import com.google.common.collect.Maps;
import com.tugos.dst.admin.entity.User;
import lombok.Data;

import java.util.Map;

/**
 * @author qinming
 * @date 2020-10-25 23:13:42
 * <p> 本地数据库 </p>
 */

@Data
public class DstConfigRoomData {

    /**
     * 房间id
     */
    public String roomId;

    /**
     * 房间名
     */
    public String roomName;

    /**
     * 定时更新游戏任务
     */
    public Map<String, Integer> SCHEDULE_UPDATE_MAP = Maps.newHashMap();

    /**
     * 定时备份游戏任务
     */
    public Map<String, Integer> SCHEDULE_BACKUP_MAP = Maps.newHashMap();

    /**
     * 不启动地面标志
     */
    public Boolean notStartMaster;

    /**
     * 不启动洞穴标志
     */
    public Boolean notStartCaves;


    /**
     * 主端口号
     */
    public String masterPort;

    /**
     * 地面端口号
     */
    public String groundPort;

    /**
     * 洞穴端口号
     */
    public String cavesPort;

    /**
     * 清理所有数据
     */
    public void clearAllData() {
        SCHEDULE_UPDATE_MAP.clear();
        SCHEDULE_BACKUP_MAP.clear();
    }

}
