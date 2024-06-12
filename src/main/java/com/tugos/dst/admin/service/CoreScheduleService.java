package com.tugos.dst.admin.service;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Range;
import com.tugos.dst.admin.enums.StartTypeEnum;
import com.tugos.dst.admin.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author qinming
 * @date 2020-05-21
 * <p>
 * 核心定时器服务，定时执行更新，备份任务
 * 启动释放管理脚本
 * 启动读取存储的数据
 * 定时将缓存写入文件
 * </p>
 */
@Service
@Slf4j
public class CoreScheduleService {

    @Value("${dst.username:admin}")
    private String dstUser;

    @Value("${dst.password:123456}")
    private String dstPassword;

    @Value("${dst.nickname:管理员}")
    private String nickname;


    private HomeService homeService;
    private ShellService shellService;
    private BackupService backupService;

    @Value("${dst.master.port:10888}")
    private String masterPort;

    @Value("${dst.ground.port:10998}")
    private String groundPort;

    @Value("${dst.caves.port:10999}")
    private String cavesPort;

    /**
     * 最大阈值 3分钟
     * 任务时间在当前时间的0到3分钟之间执行
     */
    public int upper = 3 * 60 * 1000;

    @Autowired
    DataService dataService;

    /**
     * 每天更新清理一下定时任务的执行次数
     */
    @Scheduled(cron = "1 0 0 * * ?")
    public void resetScheduleMap() {

        Map<String, DstConfigRoomData> roomInfoMap = dataService.getRoomInfoMap();
        for (Map.Entry<String, DstConfigRoomData> roomInfo : roomInfoMap.entrySet()) {
            Set<String> backupKeySet = roomInfo.getValue().SCHEDULE_BACKUP_MAP.keySet();
            for (String key : backupKeySet) {
                roomInfo.getValue().SCHEDULE_BACKUP_MAP.put(key, 0);
            }
            Set<String> updateKeySet = roomInfo.getValue().SCHEDULE_UPDATE_MAP.keySet();
            for (String key : updateKeySet) {
                roomInfo.getValue().SCHEDULE_UPDATE_MAP.put(key, 0);
            }
        }
        dataService.updateRoomInfoMap(roomInfoMap);


    }

    /**
     * 智能更新，每30分钟检查一下最新版本
     */
    @Scheduled(fixedDelay = 1000 * 60 * 30, initialDelay = 1000 * 60 * 30)
    public void smartUpdateGame() {
        Boolean smartUpdate = dataService.getSmartUpdate();
        if (smartUpdate != null && smartUpdate) {
            String steamVersion = DstVersionUtils.getSteamVersionV3();
            String localVersion = DstVersionUtils.getLocalVersion();
            if (StringUtils.isNoneBlank(steamVersion, localVersion)) {
                long sv = Long.parseLong(steamVersion);
                long lv = Long.parseLong(localVersion);
                if (sv > lv) {
                    log.info("智能更新进行...");
                    for (Map.Entry<String, DstConfigRoomData> roomInfo : dataService.getRoomInfoMap().entrySet()) {
                        onlyUpdateGame(roomInfo.getValue());
                    }

                }
            } else {
                log.info("拿不到最新的版本号：steamVersion={},localVersion={}", steamVersion, localVersion);
            }
        }
    }

    /**
     * 定时任务每5秒执行一次,第一次延长10秒
     */
    @Scheduled(fixedDelay = 5 * 1000, initialDelay = 10 * 1000)
    public void scheduleExe() {
        this.backupGame();
        this.updateGame();
        //将数据存储到文件中
//        DBUtils.saveDataToFile();
    }


    /**
     * 更新游戏任务
     */
    public void updateGame() {
        Date currentDate = new Date();
        String currentDateStr = DateUtil.format(currentDate, DatePattern.NORM_DATE_PATTERN);
        Map<String, DstConfigRoomData> roomInfoMap = dataService.getRoomInfoMap();
        for (Map.Entry<String, DstConfigRoomData> roomInfo : roomInfoMap.entrySet()) {
            Set<String> updateListTime = roomInfo.getValue().SCHEDULE_UPDATE_MAP.keySet();
            if (CollectionUtils.isNotEmpty(updateListTime)) {
                updateListTime.forEach(time -> {
                    Integer count = roomInfo.getValue().SCHEDULE_UPDATE_MAP.get(time);
                    if (count < 1) {
                        DateTime parse = DateUtil.parse(currentDateStr + " " + time, DatePattern.NORM_DATETIME_PATTERN);
                        long execTime = parse.getTime();
                        long currentDateTime = currentDate.getTime();
                        long subTime = currentDateTime - execTime;
                        if (Range.open(0, upper).contains((int) subTime)) {
                            log.info("定时更新并重启游戏");
                            this.onlyUpdateGame(roomInfo.getValue());
                            //记录执行次数
                            roomInfo.getValue().SCHEDULE_UPDATE_MAP.put(time, 1);
                        }
                    }
                });
            }
        }

        dataService.updateRoomInfoMap(roomInfoMap);
    }

    private void onlyUpdateGame(DstConfigRoomData roomInfo) {
        shellService.sendBroadcast("服务器将马上进行更新，你将与服务器断开连接(The server will be updated immediately)", roomInfo.roomId);
        shellService.sendBroadcast("请稍后再进入房间(Please enter the room later)", roomInfo.roomId);
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        homeService.updateGame(roomInfo.roomId);
        boolean notStartMaster = roomInfo.notStartMaster != null ? roomInfo.notStartMaster : false;
        boolean notStartCaves = roomInfo.notStartCaves != null ? roomInfo.notStartCaves : false;
        if (!notStartMaster && !notStartCaves) {
            //全启动
            homeService.start(StartTypeEnum.START_ALL.type, roomInfo.roomId);
        }
        if (notStartMaster && !notStartCaves) {
            //不启动地面
            homeService.start(StartTypeEnum.START_CAVES.type, roomInfo.roomId);
        }
        if (!notStartMaster && notStartCaves) {
            //不启动洞穴
            homeService.start(StartTypeEnum.START_MASTER.type, roomInfo.roomId);
        }
        if (notStartMaster && notStartCaves) {
            //都不启动
        }
    }


    /**
     * 备份游戏任务
     */
    public void backupGame() {
        Date currentDate = new Date();
        String currentDateStr = DateUtil.format(currentDate, DatePattern.NORM_DATE_PATTERN);
        Map<String, DstConfigRoomData> roomInfoMap = dataService.getRoomInfoMap();
        for (Map.Entry<String, DstConfigRoomData> roomInfo : roomInfoMap.entrySet()) {
            Set<String> backupListTime = roomInfo.getValue().SCHEDULE_BACKUP_MAP.keySet();
            //执行备份任务
            if (CollectionUtils.isNotEmpty(backupListTime)) {
                backupListTime.forEach(time -> {
                    Integer count = roomInfo.getValue().SCHEDULE_BACKUP_MAP.get(time);
                    if (count < 1) {
                        DateTime parse = DateUtil.parse(currentDateStr + " " + time, DatePattern.NORM_DATETIME_PATTERN);
                        long execTime = parse.getTime();
                        long currentDateTime = currentDate.getTime();
                        long subTime = currentDateTime - execTime;
                        if (Range.open(0, upper).contains((int) subTime)) {
                            log.info("定时备份游戏");
                            backupService.backup(null, roomInfo.getValue().getRoomId());
                            roomInfo.getValue().SCHEDULE_BACKUP_MAP.put(time, 1);
                        }
                    }
                });
            }
        }
        dataService.updateRoomInfoMap(roomInfoMap);

    }


    /**
     * 启动时释放安装dst游戏脚本
     * 读取文件中存储的数据到缓存中
     */
    @PostConstruct
    public void initSystem() throws Exception {

        //释放脚本并授权
        copyAndChmod(DstConstant.INSTALL_DST);
        copyAndChmod(DstConstant.DST_START);
    }

    /**
     * 释放脚本并授权
     */
    private void copyAndChmod(String fileName) throws Exception {
        boolean copy = FileUtils.fileShellCopy(fileName);
        if (copy) {
            FileUtils.chmod(fileName);
        }
    }

    @Autowired
    public void setHomeService(HomeService homeService) {
        this.homeService = homeService;
    }

    @Autowired
    public void setShellService(ShellService shellService) {
        this.shellService = shellService;
    }

    @Autowired
    public void setBackupService(BackupService backupService) {
        this.backupService = backupService;
    }
}
