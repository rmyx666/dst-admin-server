package com.tugos.dst.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @author qinming
 * @date 2020-10-27 22:54:49
 * <p> 定时任务信息 </p>
 */
@Data
public class ScheduleVO {

    /**
     * 更新游戏时间
     */
    private List<InnerData> backupTimeList;
    /**
     * 智能更新mod标志
     */
    private Boolean smartUpdateMod;
    /**
     * 更新游戏mod时间列表
     */
    private List<InnerData> updateModTimeList;
    /**
     * 智能更新服务器标志
     */
    private Boolean smartUpdateServer;
    /**
     * 更新游戏服务器时间列表
     */
    private List<InnerData> updateServerTimeList;

    /**
     * 自动启动地面标志
     */
    private Boolean autoStartMaster;

    /**
     * 自动启动洞穴标志
     */
    private Boolean autoStartCaves;

    /**
     * 自动重置世界
     */
    private Boolean autoRegenerate;


    @Data
    public static class InnerData {
        /**
         * 时间
         */
        private String time;
        /**
         * 次数
         */
        private int count;
    }

}
