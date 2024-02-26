package com.tugos.dst.admin.vo;

import lombok.Data;

import java.util.List;


/**
 * @author qinming
 * @date 2022-11-21 20:57:57
 * <p> 游戏的存档信息 服务器使用详细 </p>
 */
@Data
public class ServerInfoVO {

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
     * 当前玩家列表
     */
    private List<String> playerList;




}
