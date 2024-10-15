package com.tugos.dst.admin.scheduler;


import com.tugos.dst.admin.service.PlayerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class SchedulerTrigger {


    @Autowired
    PlayerService playerService;
    /**
     * 定时任务每60秒执行一次,第一次延长10秒
     */
    @Scheduled(fixedDelay = 60 * 1000, initialDelay = 10 * 1000)
    public void runScreenScheduler() throws Exception {
        playerService.savePlayerLog();
    }





}
