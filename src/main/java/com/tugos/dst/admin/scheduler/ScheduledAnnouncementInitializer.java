package com.tugos.dst.admin.scheduler;

import com.tugos.dst.admin.entity.ScheduledAnnouncement;
import com.tugos.dst.admin.service.ScheduledAnnouncementService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author wgr
 * @Title 定时公告初始化器
 * @Description 应用启动时初始化定时公告系统
 * @date 2025/12/15
 */
@Component
@Log4j2
public class ScheduledAnnouncementInitializer {

    @Autowired
    private DynamicSchedulerManager dynamicSchedulerManager;

    @Autowired
    private ScheduledAnnouncementService scheduledAnnouncementService;

    /**
     * 应用启动时初始化定时公告
     */
    @PostConstruct
    public void initScheduledAnnouncements() {
        try {
            // 从数据库加载所有启用的公告
            List<ScheduledAnnouncement> enabledAnnouncements = scheduledAnnouncementService.getEnabledAnnouncements();

            // 为每条启用的公告启动定时任务
            for (ScheduledAnnouncement announcement : enabledAnnouncements) {
                dynamicSchedulerManager.scheduleAnnouncement(announcement);
            }

            log.info("定时公告系统已初始化，共启用 {} 条公告", enabledAnnouncements.size());
        } catch (Exception e) {
            log.error("初始化定时公告系统失败", e);
        }
    }
}
