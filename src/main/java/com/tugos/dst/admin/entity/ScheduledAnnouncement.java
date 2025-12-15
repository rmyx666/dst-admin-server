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
 * @Description 定时公告配置表
 * @date 2025/12/15
 */
@Data
@TableName("scheduled_announcement")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScheduledAnnouncement implements Serializable {

    @TableId
    private Long id;

    // 房间ID
    private String roomId;

    // 公告名称
    private String announcementName;

    // 发送间隔（毫秒）
    private Long intervalMs;

    // 公告文字内容
    private String announcementText;

    // 是否启用
    private Boolean isEnabled;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    // 备注
    private String remarks;
}
