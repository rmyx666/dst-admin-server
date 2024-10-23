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
     * 更新游戏时间列表
     */
    private List<InnerData> updateTimeList;

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

    /**
     * 智能更新标志
     */
    private Boolean smartUpdate;


    @Data
    public static class InnerData{
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
