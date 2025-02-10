package com.tugos.dst.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.TreeMap;

/**
 * @Description 房间信息实体类，包含房间相关配置和操作。
 * @Author wgr
 * @Date 2025/2/7 18:00
 */
@Data
@TableName(value = "room_info", autoResultMap = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfo implements Serializable {

    @TableId
    /** 房间ID */
    public String roomId;

    /** 房间名称 */
    public String roomName;

    /** 智能更新mod标志 */
    private Boolean smartUpdateMod;

    @TableField(typeHandler = JacksonTypeHandler.class)
    /** 定时更新Mod任务 */
    public TreeMap<String, Integer> scheduleUpdateModMap;

    /** 智能更新服务器标志 */
    private Boolean smartUpdateServer;

    /** 更新游戏服务器时间列表 */
    @TableField(typeHandler = JacksonTypeHandler.class)
    public TreeMap<String, Integer> scheduleUpdateServerMap;

    @TableField(typeHandler = JacksonTypeHandler.class)
    /** 定时备份游戏任务 */
    public TreeMap<String, Integer> scheduleBackupMap;

    /** 自动启动地面标志 */
    public Boolean autoStartMaster;

    /** 自动启动洞穴标志 */
    public Boolean autoStartCaves;

    /** 主端口号 */
    public String masterPort;

    /** 地面端口号 */
    public String groundPort;

    /** 洞穴端口号 */
    public String cavesPort;

    /** 自动重启游戏标志 */
    public Boolean autoRegenerate;

    /**
     * 清理所有数据，包括定时更新任务和备份任务。
     */
    public void clearAllData() {
        if (scheduleUpdateModMap != null) {
            scheduleUpdateModMap.clear();
        }
        if (scheduleBackupMap != null) {
            scheduleBackupMap.clear();
        }
        if (scheduleUpdateServerMap != null) {
            scheduleUpdateServerMap.clear();
        }
    }
}
