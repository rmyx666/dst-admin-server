package com.tugos.dst.admin.scheduler;

import com.tugos.dst.admin.entity.ScheduledAnnouncement;
import com.tugos.dst.admin.service.ShellService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author wgr
 * @Title 动态定时任务管理器
 * @Description 管理定时公告的动态启动、更新、取消
 * @date 2025/12/15
 */
@Component
@Log4j2
public class DynamicSchedulerManager {

    @Autowired
    private ShellService shellService;

    private ThreadPoolTaskScheduler taskScheduler;

    // 存储公告ID和其对应的定时任务
    private final Map<Long, ScheduledFuture<?>> announcementTasks = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(10);
        taskScheduler.setThreadNamePrefix("scheduled-announcement-");
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);
        taskScheduler.setAwaitTerminationSeconds(30);
        taskScheduler.initialize();
    }

    /**
     * 启动定时公告任务
     */
    public void scheduleAnnouncement(ScheduledAnnouncement announcement) {
        if (announcement == null || !announcement.getIsEnabled()) {
            return;
        }

        try {
            Long id = announcement.getId();
            // 如果已存在，先取消旧的任务
            if (announcementTasks.containsKey(id)) {
                cancelAnnouncement(id);
            }

            // 创建并启动新的定时任务
            ScheduledFuture<?> future = taskScheduler.scheduleAtFixedRate(
                    () -> executeAnnouncement(announcement),
                    announcement.getIntervalMs()
            );

            announcementTasks.put(id, future);
            log.info("公告 {} (ID: {}) 已启动，房间ID: {}，间隔：{}ms",
                    announcement.getAnnouncementName(), id, announcement.getRoomId(), announcement.getIntervalMs());
        } catch (Exception e) {
            log.error("启动公告任务失败: " + announcement.getAnnouncementName(), e);
        }
    }

    /**
     * 取消定时公告任务
     */
    public void cancelAnnouncement(Long announcementId) {
        try {
            ScheduledFuture<?> future = announcementTasks.get(announcementId);
            if (future != null && !future.isCancelled()) {
                future.cancel(false);
                announcementTasks.remove(announcementId);
                log.info("公告 ID: {} 已取消", announcementId);
            }
        } catch (Exception e) {
            log.error("取消公告任务失败: " + announcementId, e);
        }
    }

    /**
     * 更新定时公告任务
     */
    public void rescheduleAnnouncement(ScheduledAnnouncement announcement) {
        cancelAnnouncement(announcement.getId());
        scheduleAnnouncement(announcement);
    }

    /**
     * 执行公告发送
     */
    private void executeAnnouncement(ScheduledAnnouncement announcement) {
        try {
            String roomId = announcement.getRoomId();
            String message = announcement.getAnnouncementText();

            // 直接使用公告中的roomId发送
            if (StringUtils.isBlank(roomId)) {
                log.warn("公告 {} 的房间ID为空，跳过发送", announcement.getAnnouncementName());
                return;
            }

            try {
                shellService.sendBroadcast(message, roomId);
            } catch (Exception e) {
                log.error("向房间 {} 发送公告失败", roomId, e);
            }
        } catch (Exception e) {
            log.error("执行公告任务失败: " + announcement.getAnnouncementName(), e);
        }
    }

    /**
     * 取消所有定时任务
     */
    public void cancelAllAnnouncements() {
        announcementTasks.forEach((id, future) -> {
            if (future != null && !future.isCancelled()) {
                future.cancel(false);
            }
        });
        announcementTasks.clear();
        log.info("所有公告任务已取消");
    }

    /**
     * 获取当前运行的任务数
     */
    public int getRunningTasksCount() {
        return announcementTasks.size();
    }

    /**
     * 检查某个公告任务是否在运行
     */
    public boolean isAnnouncementRunning(Long announcementId) {
        ScheduledFuture<?> future = announcementTasks.get(announcementId);
        return future != null && !future.isCancelled();
    }
}
