package com.tugos.dst.admin.utils;

import com.google.common.collect.Maps;
import com.tugos.dst.admin.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author qinming
 * @date 2020-10-25 23:13:42
 * <p> 本地数据库 </p>
 */

@Data
public class DstConfigRoomData implements Serializable {

    @ApiModelProperty(value = "房间ID", required = true, hidden = false)
    public String roomId;

    @ApiModelProperty(value = "房间名称", required = true, hidden = false)
    public String roomName;

    @ApiModelProperty(value = "定时更新游戏任务", required = false, hidden = true)
    public Map<String, Integer> SCHEDULE_UPDATE_MAP;

    @ApiModelProperty(value = "定时备份游戏任务", required = false, hidden = true)
    public Map<String, Integer> SCHEDULE_BACKUP_MAP;

    @ApiModelProperty(value = "不启动地面标志", required = false, hidden = true)
    public Boolean notStartMaster;

    @ApiModelProperty(value = "不启动洞穴标志", required = false, hidden = true)
    public Boolean notStartCaves;

    @ApiModelProperty(value = "主端口号", required = true, hidden = false)
    public String masterPort;

    @ApiModelProperty(value = "地面端口号", required = true, hidden = false)
    public String groundPort;

    @ApiModelProperty(value = "洞穴端口号", required = true, hidden = false)
    public String cavesPort;

    /**
     * 清理所有数据
     */
    public void clearAllData() {
        SCHEDULE_UPDATE_MAP.clear();
        SCHEDULE_BACKUP_MAP.clear();
    }

}
