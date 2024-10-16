package com.tugos.dst.admin.vo;

import com.tugos.dst.admin.entity.PlayerLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnlineTrendVo {
    private String timePeriod;  // 时间段（分钟、小时、天）
    private Integer count;      // 玩家数量（去重后的）
    private List<PlayerLog> playerLogs;  // 去重后的玩家日志列表
}
