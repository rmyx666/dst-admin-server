package com.tugos.dst.admin.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.tugos.dst.admin.vo.ScheduleVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.TreeMap;

/**

 * @Description 房间信息
 * @author wgr

 * @date 2025/2/7 18:00
 */

@Data
@TableName(value = "room_info", autoResultMap = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfo implements Serializable {
    @TableId
    //@ApiModelProperty(value = "房间ID", required = true, hidden = false)
    public String roomId;

    //@ApiModelProperty(value = "房间名称", required = true, hidden = false)
    public String roomName;

    /**
     * 智能更新mod标志
     */
    private Boolean smartUpdateMod;

    @TableField(typeHandler = JacksonTypeHandler.class)  // 指定使用 JSON 序列化
    //@ApiModelProperty(value = "定时更新Mod任务", required = false, hidden = true)
    public TreeMap<String, Integer> scheduleUpdateModMap;


    /**
     * 智能更新服务器标志
     */
    private Boolean smartUpdateServer;
    /**
     * 更新游戏服务器时间列表
     */
    @TableField(typeHandler = JacksonTypeHandler.class)  // 指定使用 JSON 序列化
    //@ApiModelProperty(value = "定时更新Mod任务", required = false, hidden = true)
    public TreeMap<String, Integer> scheduleUpdateServerMap;


    @TableField(typeHandler = JacksonTypeHandler.class)
    //@ApiModelProperty(value = "定时备份游戏任务", required = false, hidden = true)
    public TreeMap<String, Integer> scheduleBackupMap;


    //@ApiModelProperty(value = "自动启动地面标志", required = false, hidden = true)
    public Boolean autoStartMaster;

    //@ApiModelProperty(value = "自动启动洞穴标志", required = false, hidden = true)
    public Boolean autoStartCaves;

    //@ApiModelProperty(value = "主端口号", required = true, hidden = false)
    public String masterPort;

    //@ApiModelProperty(value = "地面端口号", required = true, hidden = false)
    public String groundPort;

    //@ApiModelProperty(value = "洞穴端口号", required = true, hidden = false)
    public String cavesPort;

    //自动重启游戏
    public Boolean autoRegenerate;

    /**
     * 清理所有数据
     */
    public void clearAllData() {
        scheduleUpdateModMap.clear();
        scheduleBackupMap.clear();
        scheduleUpdateServerMap.clear();
    }

}
