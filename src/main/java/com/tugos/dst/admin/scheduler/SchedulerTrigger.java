package com.tugos.dst.admin.scheduler;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Range;
import com.tugos.dst.admin.dao.DstConfigRoomDataMapper;
import com.tugos.dst.admin.dao.PlayerLogMapper;
import com.tugos.dst.admin.entity.PlayerLog;
import com.tugos.dst.admin.entity.RoomInfo;
import com.tugos.dst.admin.enums.StartTypeEnum;
import com.tugos.dst.admin.logger.LoggerUtil;
import com.tugos.dst.admin.service.*;
import com.tugos.dst.admin.utils.*;
import com.tugos.dst.admin.vo.GameSnapshotVO;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wgr
 * @Title 定时任务启动器
 * @date 2024/10/17 15:09
 */
@Component
@Log4j2
public class SchedulerTrigger {


    @Autowired
    PlayerService playerService;
    @Autowired
    private HomeService homeService;
    @Autowired
    private ShellService shellService;
    @Autowired
    private BackupService backupService;

    @Autowired
    JavaProgramUpdateUtil javaProgramUpdateUtil;

    @Autowired
    DstConfigRoomDataMapper dstConfigRoomDataMapper;

    /**
     * @return void
     * @Title runScreenScheduler
     * @Description 定时获取当前在线的玩家信息并保存到数据库中 定时任务每60秒执行一次,第一次延长10秒
     * @author wgr
     * @date 2024/10/17 15:10
     */
    @Scheduled(fixedDelay = 60 * 1000, initialDelay = 10 * 1000)
    public void runScreenScheduler() throws Exception {
        playerService.savePlayerLog();
    }


    /**
     * 最大阈值 3分钟
     * 任务时间在当前时间的0到3分钟之间执行
     */
    public int upper = 3 * 60 * 1000;

    @Autowired
    DataService dataService;
    @Autowired
    PlayerLogMapper playerLogMapper;

    /**
     * 每十分钟发送一个公告广播
     */
    @Scheduled(fixedRate = 600000)
    public void sendMsg() throws InterruptedException {

        List<String> collect = dstConfigRoomDataMapper.selectList(null).stream().map(x -> x.getRoomId()).collect(Collectors.toList());
        String message = "\uDB80\uDC0D 玩得开心可以加QQ群一起玩呀 683251529 \uDB80\uDC0D";
        for (String roomId : collect) {
            shellService.sendBroadcast(message, roomId);
            Thread.sleep(1000);
        }

    }

    /**
     * 每天更新清理一下定时任务的执行次数
     */
    @Scheduled(cron = "1 0 0 * * ?")
    public void resetScheduleMap() {

        List<RoomInfo> roomData = dstConfigRoomDataMapper.selectList(null);
        for (RoomInfo roomInfo : roomData) {
            Set<String> backupKeySet = roomInfo.scheduleBackupMap.keySet();
            for (String key : backupKeySet) {
                roomInfo.scheduleBackupMap.put(key, 0);
            }
            Set<String> updateKeySet = roomInfo.scheduleUpdateMap.keySet();
            for (String key : updateKeySet) {
                roomInfo.scheduleUpdateMap.put(key, 0);
            }
            dstConfigRoomDataMapper.updateById(roomInfo);
        }


    }

    /**
     * 每天1点更新java程序
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void updateJavaProgram() {
        javaProgramUpdateUtil.updateJavaProgram();
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
                    List<RoomInfo> roomData = dstConfigRoomDataMapper.selectList(null);
                    for (RoomInfo roomInfo : roomData) {
                        onlyUpdateGame(roomInfo);
                    }

                }
            } else {
                log.info("拿不到最新的版本号：steamVersion={},localVersion={}", steamVersion, localVersion);
            }
        }
    }

    /**
     * 定时任务每60秒执行一次,第一次延长10秒
     */
    @Scheduled(fixedDelay = 60 * 1000, initialDelay = 10 * 1000)
    public void scheduleExe() {
        this.backupGame();
        this.updateGame();

    }


    /**
     * 更新游戏任务
     */
    public void updateGame() {
        Date currentDate = new Date();
        String currentDateStr = DateUtil.format(currentDate, DatePattern.NORM_DATE_PATTERN);
        List<RoomInfo> roomData = dstConfigRoomDataMapper.selectList(null);
        for (RoomInfo roomInfo : roomData) {
            Set<String> updateListTime = roomInfo.scheduleUpdateMap.keySet();
            if (CollectionUtils.isNotEmpty(updateListTime)) {
                updateListTime.forEach(time -> {
                    Integer count = roomInfo.scheduleUpdateMap.get(time);
                    if (count < 1) {
                        DateTime parse = DateUtil.parse(currentDateStr + " " + time, DatePattern.NORM_DATETIME_PATTERN);
                        long execTime = parse.getTime();
                        long currentDateTime = currentDate.getTime();
                        long subTime = currentDateTime - execTime;
                        if (Range.open(0, upper).contains((int) subTime)) {
                            log.info("定时更新并重启游戏");

                            List<String> playerList = null;
                            try {
                                playerList = shellService.getPlayerList(roomInfo.getRoomId());
                                if (CollectionUtils.isEmpty(playerList)) {
                                    this.onlyUpdateGame(roomInfo);
                                    //记录执行次数
                                    roomInfo.scheduleUpdateMap.put(time, 1);
                                } else {
                                    log.info("当前时间：" + new Date().toString() + "房间ID：" + roomInfo.getRoomId() + "房间内存在玩家正在游玩，暂不更新");
                                }
                            } catch (Exception e) {
                                log.error("更新时获取玩家列表失败或者其他原因导致失败");
                            }


                        }
                    }
                });
            }
            dstConfigRoomDataMapper.updateById(roomInfo);
        }


    }

    private void onlyUpdateGame(RoomInfo roomInfo) {
        shellService.sendBroadcast("服务器将马上进行更新，你将与服务器断开连接(The server will be updated immediately)", roomInfo.roomId);
        shellService.sendBroadcast("请稍后再进入房间(Please enter the room later)", roomInfo.roomId);
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        homeService.updateGame(roomInfo.roomId);
        boolean autoStartMaster = roomInfo.autoStartMaster != null ? roomInfo.autoStartMaster : true;
        boolean autoStartCaves = roomInfo.autoStartCaves != null ? roomInfo.autoStartCaves : true;
        if (autoStartMaster && autoStartCaves) {
            //全启动
            homeService.start(StartTypeEnum.START_ALL.type, roomInfo.roomId);
        }
        if (!autoStartMaster && autoStartCaves) {
            //不启动地面
            homeService.start(StartTypeEnum.START_CAVES.type, roomInfo.roomId);
        }
        if (autoStartMaster && !autoStartCaves) {
            //不启动洞穴
            homeService.start(StartTypeEnum.START_MASTER.type, roomInfo.roomId);
        }
        if (!autoStartMaster && !autoStartCaves) {
            //都不启动
        }
    }


    /**
     * 备份游戏任务
     */
    public void backupGame() {
        Date currentDate = new Date();
        String currentDateStr = DateUtil.format(currentDate, DatePattern.NORM_DATE_PATTERN);
        List<RoomInfo> roomData = dstConfigRoomDataMapper.selectList(null);
        for (RoomInfo roomInfo : roomData) {
            Set<String> backupListTime = roomInfo.scheduleBackupMap.keySet();
            //执行备份任务
            if (CollectionUtils.isNotEmpty(backupListTime)) {
                backupListTime.forEach(time -> {
                    Integer count = roomInfo.scheduleBackupMap.get(time);
                    if (count < 1) {
                        DateTime parse = DateUtil.parse(currentDateStr + " " + time, DatePattern.NORM_DATETIME_PATTERN);
                        long execTime = parse.getTime();
                        long currentDateTime = currentDate.getTime();
                        long subTime = currentDateTime - execTime;
                        if (Range.open(0, upper).contains((int) subTime)) {
                            log.info("定时备份游戏");
                            backupService.backup(null, roomInfo.getRoomId());
                            roomInfo.scheduleBackupMap.put(time, 1);
                        }
                    }
                });
            }
            dstConfigRoomDataMapper.updateById(roomInfo);
            LoggerUtil.systemLog("重置房间："+roomInfo.roomId);
        }


    }


    /**
     * @return void
     * @Title autoRegenerateEveryday
     * @Description 如果游戏时长大于0小于40天，且三天内用户在线时长少于60分钟，重置该世界，每天晚上六点判定一次
     * @author wgr
     * @date 2024/10/23 10:55
     */
    @Scheduled(cron = "0 0 18 * * ?")
    public void autoRegenerateEveryday() {
        List<RoomInfo> roomData = dstConfigRoomDataMapper.selectList(null);
        for (RoomInfo roomInfo : roomData) {
            if (roomInfo.autoRegenerate) {


                // 获取当前时间和一个月前的时间
                Date now = new Date();
                Date oneMonthAgo = Date.from(now.toInstant().minusSeconds(3L * 24 * 60 * 60)); // 30天前

                // 构建查询条件
                QueryWrapper<PlayerLog> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("room_id", roomInfo.roomId)
                        .between("create_time", oneMonthAgo, now);

                // 获取玩家日志
                Long playerLogCount = playerLogMapper.selectCount(queryWrapper);

                Integer playDay = 0;

                GameSnapshotVO gameSnapshot = backupService.getGameSnapshot(roomInfo.roomId);
                if (gameSnapshot != null) {
                    playDay = Integer.valueOf(gameSnapshot.getPlayDay());
                }

                if (playerLogCount < 60 && playDay > 0 && playDay < 40) {
                    shellService.regenerate(roomInfo.roomId);
                    LoggerUtil.systemLog("重置房间："+roomInfo.roomId);
                }
            }
        }
    }

    /**
     * @return void
     * @Title autoRegenerateFriday
     * @Description 如果游戏7天内没有人进入，重置该世界，每周五晚上六点判定一次
     * @author wgr
     * @date 2024/10/23 10:56
     */
    @Scheduled(cron = "0 0 18 ? * FRI")
    public void autoRegenerateFriday() {
        List<RoomInfo> roomData = dstConfigRoomDataMapper.selectList(null);
        for (RoomInfo roomInfo : roomData) {
            if (roomInfo.autoRegenerate) {


                // 获取当前时间和一个月前的时间
                Date now = new Date();
                Date oneMonthAgo = Date.from(now.toInstant().minusSeconds(7L * 24 * 60 * 60)); // 30天前

                // 构建查询条件
                QueryWrapper<PlayerLog> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("room_id", roomInfo.roomId)
                        .between("create_time", oneMonthAgo, now);

                // 获取玩家日志
                Long playerLogCount = playerLogMapper.selectCount(queryWrapper);


                if (playerLogCount == 0) {
                    shellService.regenerate(roomInfo.roomId);
                }
            }
        }
    }
}
