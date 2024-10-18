package com.tugos.dst.admin.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@TableName("user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    @TableId
    private String username;

    private String password;

    private String nickname;

    private String picture;

}
