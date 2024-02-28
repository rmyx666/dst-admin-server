package com.tugos.dst.admin.vo;

import lombok.Data;

import java.util.List;


/**
 * @author qinming
 * @date 2022-11-21 20:57:57
 * <p> 游戏的存档信息 服务器使用详细 </p>
 */
@Data
public class RoomInfoVO extends ServerInfoVO{

    /**
     * 房间id
     */
    public String roomId;

    /**
     * 房间名
     */
    public String roomName;

    /**
     * 主端口号
     */
    public String masterPort;

    /**
     * 地面端口号
     */
    public String groundPort;

    /**
     * 洞穴端口号
     */
    public String cavesPort;

}
