package com.tugos.dst.admin.controller;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.entity.ScheduledAnnouncement;
import com.tugos.dst.admin.scheduler.DynamicSchedulerManager;
import com.tugos.dst.admin.service.ScheduledAnnouncementService;
import com.tugos.dst.admin.service.SystemService;
import com.tugos.dst.admin.vo.GamePortVO;
import com.tugos.dst.admin.vo.ScheduleVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author qinming
 * @date 2020-10-25 10:57:45
 * <p> 系统服务器控制器 </p>
 */
@Controller
@Slf4j
@RequestMapping("/system")
public class SystemController {

    private SystemService systemService;

    @Autowired
    private ScheduledAnnouncementService scheduledAnnouncementService;

    @Autowired
    private DynamicSchedulerManager dynamicSchedulerManager;

    /**
     * 系统设置页
     */
    @GetMapping("/index")
    @RequiresAuthentication
    public String index() {
        return "system/index";
    }

    /**
     * 向导页面
     */
    @GetMapping("/guide")
    public String toGuide() {
        Locale locale = LocaleContextHolder.getLocale();
        if (Locale.CHINA.getLanguage().equals(locale.getLanguage())) {
            //中文语言
            return "system/guide";
        } else {
            return "system/guide_en";
        }
    }

    @GetMapping("/about")
    public String toAbout() {
        return "/system/about";
    }

    @GetMapping("/getDstLog")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<List<String>> getDstLog(@RequestParam(required = false, defaultValue = "0") Integer type,
                                            @RequestParam(required = false, defaultValue = "100") Integer rowNum,
                                            @RequestParam(required = true) String roomId) {
        log.info("拉取饥荒的日志：type={},rowNum={}", type, rowNum);
        return ResultVO.data(systemService.getDstLog(type, rowNum, roomId));
    }

    /**
     * 获取任务时间列表
     */
    @GetMapping("/getScheduleList")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<ScheduleVO> getScheduleList(@RequestParam(required = true) String roomId) {
        return ResultVO.data(systemService.getScheduleList(roomId));
    }

    @PostMapping("/saveSchedule")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> saveSchedule(@RequestBody ScheduleVO vo,
                                         @RequestParam(required = true) String roomId) {
        systemService.saveSchedule(vo, roomId);
        return ResultVO.success();
    }


    @GetMapping("/getVersion")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<Map<String, String>> getVersion() {
        return ResultVO.data(systemService.getVersion());

    }


    @GetMapping("/getGamePort")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<GamePortVO> getGamePort(@RequestParam(required = true) String roomId) {
        return ResultVO.data(systemService.getGamePort(roomId));

    }


    @PostMapping("/saveGamePort")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<Map<String, String>> saveGamePort(@RequestBody GamePortVO gamePortVO,
                                                      @RequestParam(required = true) String roomId) {
        systemService.saveGamePort(gamePortVO, roomId);
        return ResultVO.success();
    }

    /**
     * 获取定时公告列表（按房间ID过滤）
     */
    @GetMapping("/announcement/list")
    @ResponseBody
//    @RequiresAuthentication
    public ResultVO<List<ScheduledAnnouncement>> getAnnouncementList(@RequestParam(required = true) String roomId) {
        return ResultVO.data(scheduledAnnouncementService.getAnnouncementsByRoomId(roomId));
    }

    /**
     * 新增定时公告
     */
    @PostMapping("/announcement/add")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> addAnnouncement(@RequestBody ScheduledAnnouncement announcement) {
        scheduledAnnouncementService.addAnnouncement(announcement);
        // 如果启用，立即启动该公告的定时任务
        if (announcement.getIsEnabled()) {
            dynamicSchedulerManager.scheduleAnnouncement(announcement);
        }
        return ResultVO.success("公告添加成功");
    }

    /**
     * 更新定时公告
     */
    @PostMapping("/announcement/update/{id}")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> updateAnnouncement(@PathVariable Long id, @RequestBody ScheduledAnnouncement announcement) {
        announcement.setId(id);
        scheduledAnnouncementService.updateAnnouncement(announcement);
        // 重新安排定时任务
        if (announcement.getIsEnabled()) {
            dynamicSchedulerManager.rescheduleAnnouncement(announcement);
        } else {
            dynamicSchedulerManager.cancelAnnouncement(id);
        }
        return ResultVO.success("公告更新成功");
    }

    /**
     * 删除定时公告
     */
    @PostMapping("/announcement/delete/{id}")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> deleteAnnouncement(@PathVariable Long id) {
        // 先取消定时任务
        dynamicSchedulerManager.cancelAnnouncement(id);
        // 再删除数据库记录
        scheduledAnnouncementService.deleteAnnouncement(id);
        return ResultVO.success("公告删除成功");
    }

    /**
     * 启用定时公告
     */
    @PostMapping("/announcement/enable/{id}")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> enableAnnouncement(@PathVariable Long id) {
        scheduledAnnouncementService.enableAnnouncement(id);
        ScheduledAnnouncement announcement = scheduledAnnouncementService.getAnnouncementById(id);
        if (announcement != null) {
            dynamicSchedulerManager.scheduleAnnouncement(announcement);
        }
        return ResultVO.success("公告已启用");
    }

    /**
     * 禁用定时公告
     */
    @PostMapping("/announcement/disable/{id}")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> disableAnnouncement(@PathVariable Long id) {
        scheduledAnnouncementService.disableAnnouncement(id);
        dynamicSchedulerManager.cancelAnnouncement(id);
        return ResultVO.success("公告已禁用");
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }
}
