package com.tugos.dst.admin.utils;

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
public class DstServerInfoData implements Serializable {



    public Long id;

    public String ip;
    public String name;


    public String username;

    private String password;

}
