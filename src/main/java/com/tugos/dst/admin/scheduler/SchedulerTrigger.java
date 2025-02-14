package com.tugos.dst.admin.scheduler;


import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Range;
import com.tugos.dst.admin.dao.RoomInfoMapper;
import com.tugos.dst.admin.dao.PlayerLogMapper;
import com.tugos.dst.admin.dao.SystemSettingMapper;
import com.tugos.dst.admin.entity.PlayerLog;
import com.tugos.dst.admin.entity.RoomInfo;
import com.tugos.dst.admin.entity.SystemSetting;
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

import java.util.*;
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
    PlayerLogService playerLogService;
    @Autowired
    private HomeService homeService;
    @Autowired
    private ShellService shellService;
    @Autowired
    private BackupService backupService;

    @Autowired
    JavaProgramUpdateUtil javaProgramUpdateUtil;

    @Autowired
    RoomInfoMapper roomInfoMapper;
    @Autowired
    RoomOperationLogService roomOperationLogService;
    @Autowired
    UpdateProgramService updateProgramService;
    /**
     * 最大阈值 3分钟
     * 任务时间在当前时间的0到3分钟之间执行
     */
    public int upper = 3 * 60 * 1000;

    @Autowired
    PlayerLogMapper playerLogMapper;
    @Autowired
    SystemSettingMapper systemSettingMapper;

    /**
     * @return void
     * @Title runScreenScheduler
     * @Description 定时任务每分钟
     * @author wgr
     * @date 2024/10/17 15:10
     */
    @Scheduled(cron = "0 * * * * ?")
    public void runScreenScheduler() throws Exception {
        Map<RoomInfo, List<PlayerLog>> allPlayerLog = playerLogService.getAllPlayerLog();
        //定时获取当前在线的玩家信息并保存到数据库中
        playerLogService.savePlayerLog(allPlayerLog);


        //每分钟保存所有房间的房间运行日志
        roomOperationLogService.saveRoomOperationLog();

//        autoStartOrStopGame(allPlayerLog);

    }

    /**
     * 每天1点更新删除一个月前的房间运行日志
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void delRoomOperationLog() {
        roomOperationLogService.delRoomOperationLog();
        playerLogService.delRoomPlayerLog();
    }




    /**
     * 每十分钟发送一个公告广播
     */
    @Scheduled(fixedRate = 600000)
    public void sendMsg() throws InterruptedException {

        SystemSetting systemSetting = systemSettingMapper.selectById(1);
        if (systemSetting.getAutoSendqq()){
            List<String> collect = roomInfoMapper.selectList(null).stream().map(x -> x.getRoomId()).collect(Collectors.toList());
            String message = "\uDB80\uDC0D 玩得开心可以加QQ群一起玩呀 683251529 \uDB80\uDC0D";
            for (String roomId : collect) {
                shellService.sendBroadcast(message, roomId);
                Thread.sleep(1000);
            }
        }



    }

    /**
     * 每天更新清理一下定时任务的执行次数
     */
    @Scheduled(cron = "1 0 0 * * ?")
    public void resetScheduleMap() {

        List<RoomInfo> roomData = roomInfoMapper.selectList(null);
        for (RoomInfo roomInfo : roomData) {
            Set<String> backupKeySet = roomInfo.scheduleBackupMap.keySet();
            for (String key : backupKeySet) {
                roomInfo.scheduleBackupMap.put(key, 0);
            }
            Set<String> updateKeySet = roomInfo.scheduleUpdateModMap.keySet();
            for (String key : updateKeySet) {
                roomInfo.scheduleUpdateModMap.put(key, 0);
            }
            roomInfoMapper.updateById(roomInfo);
        }


    }

    /**
     * 每天1点更新java程序
     */
//    @Scheduled(cron = "0 0 1 * * ?")
//    public void updateJavaProgram() {
//        javaProgramUpdateUtil.updateJavaProgram();
//    }

    /**
     * 智能更新，每30分钟向子服务器发送ip地址 延迟5分钟
     */
    @Scheduled(fixedDelay = 1000 * 60 * 30, initialDelay = 1000 * 60 * 5)
    public void autosendMasterProgramIp() throws Exception {
        updateProgramService.sendMasterProgramIp();
    }
    /**
     * 智能更新，每30分钟丛主服务器获取最近的jar包 延迟三十分钟
     */
    @Scheduled(fixedDelay = 1000 * 60 * 30, initialDelay = 1000 * 60 * 30)
    public void autoUpdateProgram() throws Exception {
        updateProgramService.autoUpdateProgram();
    }

    /**
     * 智能更新，每30分钟检查一下最新版本
     */
    @Scheduled(fixedDelay = 1000 * 60 * 30, initialDelay = 1000 * 60 * 30)
    public void smartUpdateGame() {

        String steamVersion = DstVersionUtils.getSteamVersionV3();
        String localVersion = DstVersionUtils.getLocalVersion();
        if (StringUtils.isNoneBlank(steamVersion, localVersion)) {
            long sv = Long.parseLong(steamVersion);
            long lv = Long.parseLong(localVersion);
            if (sv > lv) {
                LoggerUtil.systemLog("智能更新进行...");

                updateServerAndRestartRoom();

            }
        } else {
            LoggerUtil.systemLog("拿不到最新的版本号：steamVersion={" + steamVersion + "},localVersion={" + localVersion + "}");

        }

    }

    /**
     * 定时任务每60秒执行一次,第一次延长10秒
     */
    @Scheduled(fixedDelay = 60 * 1000, initialDelay = 10 * 1000)
    public void scheduleExe() {
        backupGame();
        updateMod();
        updateServer();
    }


    /**
     * 更新游戏mod
     */
    public void updateMod() {
        Date currentDate = new Date();
        String currentDateStr = DateUtil.format(currentDate, DatePattern.NORM_DATE_PATTERN);
        List<RoomInfo> roomData = roomInfoMapper.selectList(null);
        for (RoomInfo roomInfo : roomData) {
            if (roomInfo.getSmartUpdateMod()) {
                Set<String> updateListTime = roomInfo.scheduleUpdateModMap.keySet();
                if (CollectionUtils.isNotEmpty(updateListTime)) {
                    updateListTime.forEach(time -> {
                        Integer count = roomInfo.scheduleUpdateModMap.get(time);
                        if (count < 1) {
                            DateTime parse = DateUtil.parse(currentDateStr + " " + time, DatePattern.NORM_DATETIME_PATTERN);
                            long execTime = parse.getTime();
                            long currentDateTime = currentDate.getTime();
                            long subTime = currentDateTime - execTime;
                            if (Range.open(0, upper).contains((int) subTime)) {
                                LoggerUtil.systemLog("定时更新Mod");

                                List<String> playerList = null;
                                try {
                                    playerList = shellService.getPlayerList(roomInfo.getRoomId());
                                    if (CollectionUtils.isEmpty(playerList)) {
                                        updateMod(roomInfo);
                                        //记录执行次数
                                        roomInfo.scheduleUpdateModMap.put(time, 1);
                                        LoggerUtil.systemLog("房间ID：" + roomInfo.getRoomId() + "更新Mod成功");
                                    } else {
                                        LoggerUtil.systemLog("房间ID：" + roomInfo.getRoomId() + "房间内存在玩家正在游玩，暂不更新");
                                    }
                                } catch (Exception e) {
                                    log.error("更新时获取玩家列表失败或者其他原因导致失败",e);
                                    LoggerUtil.systemLog("更新时获取玩家列表失败或者其他原因导致失败");
                                }


                            }
                        }
                    });
                }
                roomInfoMapper.updateById(roomInfo);
            }
        }


    }


    /**
     * @param roomInfo
     * @return void
     * @Title UpdateMod
     * @Description 重启指定的房间更新mod
     * @author wgr
     * @date 2025/2/7 17:31
     */
    private void updateMod(RoomInfo roomInfo) {
        shellService.sendBroadcast("服务器将马上进行更新，你将与服务器断开连接(The server will be updated immediately)", roomInfo.roomId);
        shellService.sendBroadcast("请稍后再进入房间(Please enter the room later)", roomInfo.roomId);
        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        homeService.restart(roomInfo);
    }

    /**
     * @return void
     * @Title updateServerAndRestartRoom
     * @Description 关闭所有服务器 更新游戏服务器版本 再重新启动所有服务器
     * @author wgr
     * @date 2025/2/7 17:20
     */
    private void updateServerAndRestartRoom() {
        List<RoomInfo> roomData = roomInfoMapper.selectList(null);

        for (RoomInfo roomDatum : roomData) {
            shellService.sendBroadcast("服务器将马上进行更新，你将与服务器断开连接(The server will be updated immediately)", roomDatum.roomId);
            shellService.sendBroadcast("请稍后再进入房间(Please enter the room later)", roomDatum.roomId);
        }

        try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (RoomInfo roomDatum : roomData) {
            homeService.stop(roomDatum);
        }

        shellService.updateServer();


        for (RoomInfo roomDatum : roomData) {
            homeService.start(roomDatum);
        }
    }


    /**
     * 备份游戏任务
     */
    public void backupGame() {
        Date currentDate = new Date();
        String currentDateStr = DateUtil.format(currentDate, DatePattern.NORM_DATE_PATTERN);
        List<RoomInfo> roomData = roomInfoMapper.selectList(null);
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
                            backupService.backup(null, roomInfo.getRoomId());
                            roomInfo.scheduleBackupMap.put(time, 1);
                        }
                    }
                });
            }
            roomInfoMapper.updateById(roomInfo);
        }
    }

    /**
     * @return void
     * @Title updateServer
     * @Description 更新游戏服务器版本
     * @author wgr
     * @date 2025/2/7 17:44
     */
    public void updateServer() {
        Date currentDate = new Date();
        String currentDateStr = DateUtil.format(currentDate, DatePattern.NORM_DATE_PATTERN);
        List<RoomInfo> roomData = roomInfoMapper.selectList(null);
        if (CollectionUtils.isNotEmpty(roomData)) {
            RoomInfo roomInfo = roomData.get(0);
            if (roomInfo.getSmartUpdateServer()) {
                Set<String> updateServerListTime = roomInfo.scheduleUpdateServerMap.keySet();
                //执行备份任务
                if (CollectionUtils.isNotEmpty(updateServerListTime)) {
                    updateServerListTime.forEach(time -> {
                        Integer count = roomInfo.scheduleUpdateServerMap.get(time);
                        if (count < 1) {
                            DateTime parse = DateUtil.parse(currentDateStr + " " + time, DatePattern.NORM_DATETIME_PATTERN);
                            long execTime = parse.getTime();
                            long currentDateTime = currentDate.getTime();
                            long subTime = currentDateTime - execTime;
                            if (Range.open(0, upper).contains((int) subTime)) {
                                LoggerUtil.systemLog("更新游戏服务器");
                                updateServerAndRestartRoom();

                                //更新所有room的状态
                                for (RoomInfo roomDatum : roomData) {
                                    roomDatum.scheduleUpdateServerMap.put(time, 1);
                                    roomInfoMapper.updateById(roomDatum);
                                }

                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * @return void
     * @Title autoRegenerateEveryday
     * @Description 如果游戏时长大于0小于40天，且三天内用户在线时长少于60分钟，重置该世界，每天晚上六点判定一次
     * @author wgr
     * @date 2024/10/23 10:55
     */
    @Scheduled(cron = "0 0 17 * * ?")
    public void autoRegenerateEveryday() {
        List<RoomInfo> roomData = roomInfoMapper.selectList(null);
        for (RoomInfo roomInfo : roomData) {
            if (roomInfo.autoRegenerate) {


                // 获取当前时间和一个月前的时间
                Date now = new Date();
                Date oneMonthAgo = Date.from(now.toInstant().minusSeconds(3L * 24 * 60 * 60)); // 3天前

                // 构建查询条件
                QueryWrapper<PlayerLog> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("room_id", roomInfo.roomId)
                        .between("create_time", oneMonthAgo, now);

                // 获取玩家日志
                Long playerLogCount = playerLogMapper.selectCount(queryWrapper);

                Integer playDay = 0;

                GameSnapshotVO gameSnapshot = backupService.getGameSnapshot(roomInfo.roomId);
                if (gameSnapshot != null && StringUtils.isNumeric(gameSnapshot.getPlayDay())) {
                    playDay = Integer.valueOf(gameSnapshot.getPlayDay());
                }

                if (playerLogCount < 60 && playDay > 0 && playDay < 40) {
                    shellService.regenerate(roomInfo.roomId);
                    LoggerUtil.systemLog("自动重置房间 房间id:" + roomInfo.roomId + " 游戏天数:" + playDay + " 三天内在线时长:" + playerLogCount + " 判定自动重置");
                } else {
                    LoggerUtil.systemLog("自动重置房间 房间id:" + roomInfo.roomId + " 游戏天数:" + playDay + " 三天内在线时长:" + playerLogCount + " 判定不自动重置");
                }
            }
        }
    }

    /**
     * @return void
     * @Title autoRegenerateFriday
     * @Description 如果游戏7天内游玩时间少于60分钟，重置该世界，每周五晚上六点判定一次
     * @author wgr
     * @date 2024/10/23 10:56
     */
    @Scheduled(cron = "0 0 18 ? * FRI")
    public void autoRegenerateFriday() {
        List<RoomInfo> roomData = roomInfoMapper.selectList(null);
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

                Integer playDay = 0;

                GameSnapshotVO gameSnapshot = backupService.getGameSnapshot(roomInfo.roomId);
                if (gameSnapshot != null && StringUtils.isNumeric(gameSnapshot.getPlayDay())) {
                    playDay = Integer.valueOf(gameSnapshot.getPlayDay());
                }

                if (playerLogCount < 60) {
                    shellService.regenerate(roomInfo.roomId);
                    LoggerUtil.systemLog("自动重置房间 房间id:" + roomInfo.roomId + " 游戏天数:" + playDay + " 七天内在线时长:" + playerLogCount + " 判定自动重置");
                } else {
                    LoggerUtil.systemLog("自动重置房间 房间id:" + roomInfo.roomId + " 游戏天数:" + playDay + " 七天内在线时长:" + playerLogCount + " 判定不自动重置");
                }
            }
        }
    }

    /**
     * @param allPlayerLog
     * @return void
     * @Title autoStartOrStopGame
     * @Description 一个服务器同时启动多个房间时，根据核心数的情况启动和关闭房间
     * @author wgr
     * @date 2024/10/28 14:17
     */
//    private void autoStartOrStopGame(Map<RoomInfo, List<PlayerLog>> allPlayerLog) {
//
//        int usedCpuNum = 0;
//        List<RoomInfo> freeUseTwoCpuRoomList = new ArrayList<>();
//        List<RoomInfo> freeUseOneCpuRoomList = new ArrayList<>();
//        for (Map.Entry<RoomInfo, List<PlayerLog>> roomInfoListEntry : allPlayerLog.entrySet()) {
//            if (CollectionUtils.isNotEmpty(roomInfoListEntry.getValue())) {
//
//                if (roomInfoListEntry.getKey().getAutoStartMaster().equals(true)) usedCpuNum++;
//                if (roomInfoListEntry.getKey().getAutoStartCaves().equals(true)) usedCpuNum++;
//            } else {
//                if (roomInfoListEntry.getKey().getAutoStartMaster().equals(true) && roomInfoListEntry.getKey().getAutoStartCaves().equals(true)) {
//                    freeUseTwoCpuRoomList.add(roomInfoListEntry.getKey());
//                } else if (roomInfoListEntry.getKey().getAutoStartMaster().equals(true)) {
//                    freeUseOneCpuRoomList.add(roomInfoListEntry.getKey());
//                }
//            }
//        }
//
//        int freeCpuNum = CPU_NUM - usedCpuNum;
//        if (freeCpuNum >= 2) {
//            start(freeUseTwoCpuRoomList);
//            start(freeUseOneCpuRoomList);
//        } else if (freeCpuNum == 1) {
//            stop(freeUseTwoCpuRoomList);
//            start(freeUseOneCpuRoomList);
//        } else {
//            stop(freeUseTwoCpuRoomList);
//            stop(freeUseOneCpuRoomList);
//        }
//
//    }
//
//    void stop(List<RoomInfo> roomInfos) {
//        for (RoomInfo roomInfo : roomInfos) {
//            boolean masterStatus = shellService.getMasterStatus(roomInfo.roomId);
//            if (masterStatus) {
//                homeService.stop(roomInfo);
//                LoggerUtil.systemLog("自动启停房间 房间id:" + roomInfo.roomId + " 因为核心数不够关闭房间");
//            }
//        }
//    }
//
//    void start(List<RoomInfo> roomInfos) {
//        for (RoomInfo roomInfo : roomInfos) {
//            boolean masterStatus = shellService.getMasterStatus(roomInfo.roomId);
//            if (!masterStatus) {
//                homeService.start(roomInfo);
//                LoggerUtil.systemLog("自动启停房间 房间id:" + roomInfo.roomId + " 核心数充足启动房间");
//            }
//        }
//    }
}
