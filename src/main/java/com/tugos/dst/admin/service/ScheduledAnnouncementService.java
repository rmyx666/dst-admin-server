package com.tugos.dst.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tugos.dst.admin.dao.ScheduledAnnouncementMapper;
import com.tugos.dst.admin.entity.ScheduledAnnouncement;
import com.tugos.dst.admin.scheduler.DynamicSchedulerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class ScheduledAnnouncementService {

    @Autowired
    private ScheduledAnnouncementMapper scheduledAnnouncementMapper;

    @Autowired
    private DynamicSchedulerManager dynamicSchedulerManager;

    /**
     * 获取所有公告
     */
    public List<ScheduledAnnouncement> getAllAnnouncements() {
        return scheduledAnnouncementMapper.selectList(null);
    }

    /**
     * 获取所有启用的公告
     */
    public List<ScheduledAnnouncement> getEnabledAnnouncements() {
        QueryWrapper<ScheduledAnnouncement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_enabled", true);
        return scheduledAnnouncementMapper.selectList(queryWrapper);
    }

    /**
     * 根据房间ID获取公告
     */
    public List<ScheduledAnnouncement> getAnnouncementsByRoomId(String roomId) {
        QueryWrapper<ScheduledAnnouncement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId);
        return scheduledAnnouncementMapper.selectList(queryWrapper);
    }

    /**
     * 根据ID获取公告
     */
    public ScheduledAnnouncement getAnnouncementById(Long id) {
        return scheduledAnnouncementMapper.selectById(id);
    }

    /**
     * 新增公告
     */
    @Transactional
    public void addAnnouncement(ScheduledAnnouncement announcement) {
        announcement.setCreatedTime(new Date());
        announcement.setUpdatedTime(new Date());
        if (announcement.getIsEnabled() == null) {
            announcement.setIsEnabled(true);
        }
        scheduledAnnouncementMapper.insert(announcement);
    }

    /**
     * 更新公告
     */
    @Transactional
    public void updateAnnouncement(ScheduledAnnouncement announcement) {
        announcement.setUpdatedTime(new Date());
        scheduledAnnouncementMapper.updateById(announcement);
    }

    /**
     * 删除公告
     */
    @Transactional
    public void deleteAnnouncement(Long id) {
        scheduledAnnouncementMapper.deleteById(id);
    }

    /**
     * 根据房间ID删除该房间的所有公告
     */
    @Transactional
    public void deleteAnnouncementsByRoomId(String roomId) {
        // 先获取该房间的所有公告
        List<ScheduledAnnouncement> announcements = getAnnouncementsByRoomId(roomId);

        // 取消所有运行中的定时任务
        for (ScheduledAnnouncement announcement : announcements) {
            dynamicSchedulerManager.cancelAnnouncement(announcement.getId());
        }

        // 删除数据库记录
        QueryWrapper<ScheduledAnnouncement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId);
        scheduledAnnouncementMapper.delete(queryWrapper);
    }

    /**
     * 启用公告
     */
    @Transactional
    public void enableAnnouncement(Long id) {
        ScheduledAnnouncement announcement = new ScheduledAnnouncement();
        announcement.setId(id);
        announcement.setIsEnabled(true);
        announcement.setUpdatedTime(new Date());
        scheduledAnnouncementMapper.updateById(announcement);
    }

    /**
     * 禁用公告
     */
    @Transactional
    public void disableAnnouncement(Long id) {
        ScheduledAnnouncement announcement = new ScheduledAnnouncement();
        announcement.setId(id);
        announcement.setIsEnabled(false);
        announcement.setUpdatedTime(new Date());
        scheduledAnnouncementMapper.updateById(announcement);
    }
}
