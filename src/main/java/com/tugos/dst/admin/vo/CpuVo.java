package com.tugos.dst.admin.vo;


import com.tugos.dst.admin.utils.Arith;

import lombok.Data;

/**
 * CPU相关信息
 *
 */
@Data
public class CpuVo {
    //@ApiModelProperty(value = "核心数")
    private int cpuNum;

    //@ApiModelProperty(value = "CPU总的使用率")
    private double total;

    //@ApiModelProperty(value = "CPU系统使用率")
    private double sys;

    //@ApiModelProperty(value = "CPU用户使用率")
    private double used;

    //@ApiModelProperty(value = "CPU当前等待率")
    private double wait;

    //@ApiModelProperty(value = "CPU当前空闲率")
    private double free;


}
