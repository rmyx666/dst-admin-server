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


@Data
@TableName("system_setting")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SystemSetting implements Serializable {

    @TableId
    private Long id;

    private String masterProgramIp;
    //是否自动发送qq群号
    private Boolean autoSendqq;


}
