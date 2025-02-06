package com.tugos.dst.admin.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * @author wgr
 * @Description 房间运行情况的日志
 * @date 2025/2/6 10:38
 */
@Data
@TableName("room_operation_log")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomOperationLog implements Serializable {

    @TableId
    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    //房间id
    private String roomId;

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
}
