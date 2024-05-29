package com.tugos.dst.admin.vo;


import com.tugos.dst.admin.utils.Arith;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 內存相关信息
 */
@Data
public class MemVo {
    @ApiModelProperty(value = "内存总量")
    private double total;

    @ApiModelProperty(value = "已用内存")
    private double used;

    @ApiModelProperty(value = "剩余内存")
    private double free;
    private double usage;


}
