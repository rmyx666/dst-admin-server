package com.tugos.dst.admin.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author qinming
 * @date 2020-10-25 23:13:42
 * <p> 本地数据库 </p>
 */

@Data
@TableName("server_info")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DstServerInfoData implements Serializable {


    @TableId
    public Long id;

    public String ip;
    public String name;


    public String username;

    private String password;

}
