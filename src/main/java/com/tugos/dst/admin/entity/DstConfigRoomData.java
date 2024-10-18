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
 * @author qinming
 * @date 2020-10-25 23:13:42
 * <p> 本地数据库 </p>
 */

@Data
@TableName("room_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DstConfigRoomData implements Serializable {
    @TableId
    //@ApiModelProperty(value = "房间ID", required = true, hidden = false)
    public String roomId;

    //@ApiModelProperty(value = "房间名称", required = true, hidden = false)
    public String roomName;

    @TableField(value = "schedule_update_map",typeHandler = JacksonTypeHandler.class)  // 指定使用 JSON 序列化
    //@ApiModelProperty(value = "定时更新游戏任务", required = false, hidden = true)
    public TreeMap<String, Integer> SCHEDULE_UPDATE_MAP;

    @TableField(value = "schedule_backup_map", typeHandler = JacksonTypeHandler.class)
    //@ApiModelProperty(value = "定时备份游戏任务", required = false, hidden = true)
    public TreeMap<String, Integer> SCHEDULE_BACKUP_MAP;

    //@ApiModelProperty(value = "不启动地面标志", required = false, hidden = true)
    public Boolean notStartMaster;

    //@ApiModelProperty(value = "不启动洞穴标志", required = false, hidden = true)
    public Boolean notStartCaves;

    //@ApiModelProperty(value = "主端口号", required = true, hidden = false)
    public String masterPort;

    //@ApiModelProperty(value = "地面端口号", required = true, hidden = false)
    public String groundPort;

    //@ApiModelProperty(value = "洞穴端口号", required = true, hidden = false)
    public String cavesPort;

    /**
     * 清理所有数据
     */
    public void clearAllData() {
        SCHEDULE_UPDATE_MAP.clear();
        SCHEDULE_BACKUP_MAP.clear();
    }

}
