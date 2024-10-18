package com.tugos.dst.admin.vo;


import lombok.Data;




/**
 * @author qinming
 * @date 2022-11-21 20:57:57
 * <p> 游戏的存档信息 服务器使用详细 </p>
 */

@Data
public class RoomInfoVO {
    private Long serverId;

    private String serverIp;

    private String serverName;

    //@ApiModelProperty(value = "房间ID")
    private String roomId;

    //@ApiModelProperty(value = "房间名称")
    private String roomName;

    //@ApiModelProperty(value = "主端口号")
    private String masterPort;

    //@ApiModelProperty(value = "地面端口号")
    private String groundPort;

    //@ApiModelProperty(value = "洞穴端口号")
    private String cavesPort;

    //@ApiModelProperty(value = "房间集群名称")
    private String clusterName;

    //@ApiModelProperty(value = "最大玩家数量")
    private Integer maxPlayers;

    //@ApiModelProperty(value = "当前玩家数量")
    private Integer nowPlayers;

    //@ApiModelProperty(value = "存档的天数")
    private String playDay;

    //@ApiModelProperty(value = "存档的季节")
    private String season;

    //@ApiModelProperty(value = "MOD总数")
    private Integer totalModNum;

    //@ApiModelProperty(value = "地面状态，true表示启动")
    private Boolean masterStatus;

    //@ApiModelProperty(value = "洞穴状态，true表示启动")
    private Boolean cavesStatus;

    //@ApiModelProperty(value = "CPU相关信息")
    private CpuVo cpu;

    //@ApiModelProperty(value = "内存相关信息")
    private MemVo mem;
}
