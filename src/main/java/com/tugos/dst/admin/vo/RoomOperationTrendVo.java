package com.tugos.dst.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tugos.dst.admin.entity.PlayerLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomOperationTrendVo {
    private String timePeriod;  // 时间段（分钟、小时、天）
    private Integer count;      // 运行状态
    /**
     * 地面状态 true 启动
     */
    private Boolean masterStatus;

    /**
     * 洞穴状态 true 启动
     */
    private Boolean cavesStatus;

    //存档内游戏天数
    private String playDay;

    @JsonIgnore
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
