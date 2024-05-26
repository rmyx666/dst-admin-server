package com.tugos.dst.admin.vo;

import com.tugos.dst.admin.entity.serverInfo.Cpu;
import com.tugos.dst.admin.entity.serverInfo.Mem;
import lombok.Data;

import java.util.List;


/**
 * @author qinming
 * @date 2022-11-21 20:57:57
 * <p> 游戏的存档信息 服务器使用详细 </p>
 */
@Data
public class RoomInfoVO{

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



    /**
     * 房间名称
     */
    private String clusterName;


    /**
     * 最大玩家数量
     */
    private Integer maxPlayers;

    /**
     * 当前玩家数量
     */
    private Integer nowPlayers;


    /**
     * 存档的天数
     */
    private String playDay;

    /**
     * 存档的季节
     */
    private String season;

    /**
     * MOD总数
     */
    private Integer totalModNum;

    /**
     * 地面状态 true 启动
     */
    private Boolean masterStatus;

    /**
     * 洞穴状态 true 启动
     */
    private Boolean cavesStatus;

    /**
     * CPU相关信息
     */
    private Cpu cpu;

    /**
     * 內存相关信息
     */
    private Mem mem;

}
