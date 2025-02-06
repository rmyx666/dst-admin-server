package com.tugos.dst.admin.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.tugos.dst.admin.config.I18nResourcesConfig;
import com.tugos.dst.admin.dao.RoomInfoMapper;
import com.tugos.dst.admin.entity.RoomInfo;
import com.tugos.dst.admin.enums.DstLogTypeEnum;
import com.tugos.dst.admin.utils.*;
import com.tugos.dst.admin.vo.GamePortVO;
import com.tugos.dst.admin.vo.ScheduleVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * @author qinming
 * @date 2020-10-25 21:46:53
 * <p> 系统服务 </p>
 */
@Service
public class SystemService {

    @Autowired
    DataService dataService;
    @Autowired
    RoomInfoMapper roomInfoMapper;
    /**
     * 拉取dst游戏日志
     *
     * @param type   0地面日志 ，1 洞穴日志 2 玩家聊天记录
     * @param rowNum 日志的行数，从后开始取
     * @return 日志
     */
    public List<String> getDstLog(Integer type, Integer rowNum, String roomId) {
        String path;
        switch (Objects.requireNonNull(DstLogTypeEnum.get(type))) {
            case CAVES_LOG:
                //洞穴
                path = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_CAVES_SERVER_LOG_PATH.replace("MyDediServer", roomId);
                break;
            case CHAT_LOG:
                //聊天
                path = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_MASTER_SERVER_CHAT_LOG_PATH.replace("MyDediServer", roomId);
                break;
            default:
                //地面
                path = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_MASTER_SERVER_LOG_PATH.replace("MyDediServer", roomId);
        }
        File file = new File(path);
        List<String> result = FileUtils.readLastNLine(file, rowNum);
        if (CollectionUtils.isEmpty(result)) {
            result = Lists.newArrayList(I18nResourcesConfig.getMessage("tip.log.not.exist") + ":" + path);
        }
        return result;
    }

    /**
     * 获取任务时间列表
     *
     * @return 数据
     */
    public ScheduleVO getScheduleList(String roomId) {
        RoomInfo roomInfo = roomInfoMapper.selectById(roomId);
        ScheduleVO data = new ScheduleVO();
        Set<String> updateSet = roomInfo.scheduleUpdateMap.keySet();
        if (CollectionUtils.isNotEmpty(updateSet)) {
            List<ScheduleVO.InnerData> updateTimeList = new ArrayList<>();
            updateSet.forEach(e -> {
                ScheduleVO.InnerData innerData = new ScheduleVO.InnerData();
                innerData.setTime(e);
                innerData.setCount(roomInfo.scheduleUpdateMap.get(e));
                updateTimeList.add(innerData);
            });
            data.setUpdateTimeList(updateTimeList);
        }
        Set<String> backupSet = roomInfo.scheduleBackupMap.keySet();
        if (CollectionUtils.isNotEmpty(backupSet)) {
            List<ScheduleVO.InnerData> backupTimeList = new ArrayList<>();
            backupSet.forEach(e -> {
                ScheduleVO.InnerData innerData = new ScheduleVO.InnerData();
                innerData.setTime(e);
                innerData.setCount(roomInfo.scheduleBackupMap.get(e));
                backupTimeList.add(innerData);
            });
            data.setBackupTimeList(backupTimeList);
        }
        data.setAutoStartMaster(roomInfo.getAutoStartMaster());
        data.setAutoStartCaves(roomInfo.getAutoStartCaves());
        data.setAutoRegenerate(roomInfo.getAutoRegenerate());
        data.setSmartUpdate(dataService.getSmartUpdate());
        return data;
    }

    /**
     * 将任务时间写入缓存中
     *
     * @param vo 提交的数据
     */
    public void saveSchedule(ScheduleVO vo, String roomId) {
        RoomInfo roomInfo = roomInfoMapper.selectById(roomId);

        roomInfo.clearAllData();

        if (CollectionUtils.isNotEmpty(vo.getBackupTimeList())) {
            //写入缓存
            List<ScheduleVO.InnerData> backupTimeList = vo.getBackupTimeList();
            for (ScheduleVO.InnerData e : backupTimeList) {
                if (StringUtils.isNotBlank(e.getTime())) {
                    DateTime parse = DateUtil.parse(e.getTime(), DatePattern.NORM_DATETIME_MINUTE_PATTERN);
                    String format = DateUtil.format(parse, DatePattern.NORM_TIME_PATTERN);
                    roomInfo.scheduleBackupMap.put(format, e.getCount());
                }
            }


        }
        if (CollectionUtils.isNotEmpty(vo.getUpdateTimeList())) {
            //写入缓存
            List<ScheduleVO.InnerData> updateTimeList = vo.getUpdateTimeList();
            for (ScheduleVO.InnerData e : updateTimeList) {
                if (StringUtils.isNotBlank(e.getTime())) {
                    DateTime parse = DateUtil.parse(e.getTime(), DatePattern.NORM_DATETIME_MINUTE_PATTERN);
                    String format = DateUtil.format(parse, DatePattern.NORM_TIME_PATTERN);
                    roomInfo.scheduleUpdateMap.put(format, e.getCount());
                }

            }

        }
        if (vo.getAutoStartMaster() != null) {
            roomInfo.autoStartMaster = vo.getAutoStartMaster();
        }
        if (vo.getAutoStartCaves() != null) {
            roomInfo.autoStartCaves = vo.getAutoStartCaves();
        }
        if (vo.getAutoRegenerate() != null) {
            roomInfo.autoRegenerate = vo.getAutoRegenerate();
        }
//        if (vo.getSmartUpdate() != null) {
//            roomInfo.smartUpdate = vo.getSmartUpdate();
//        } else {
//            roomInfo.smartUpdate = false;
//        }
     roomInfoMapper.updateById(roomInfo);
    }

    /**
     * 获取服务器的版本号
     *
     * @return 版本号
     */
    public Map<String, String> getVersion() {
        String steamVersion = DstVersionUtils.getSteamVersionV3();
        String localVersion = DstVersionUtils.getLocalVersion();
        Map<String, String> map = new HashMap<>(16);
        map.put("steamVersion", steamVersion);
        map.put("localVersion", localVersion);
        return map;
    }


    public GamePortVO getGamePort(String roomId) {
        GamePortVO gamePortVO = new GamePortVO();
        RoomInfo roomInfo = roomInfoMapper.selectById(roomId);
        gamePortVO.setMasterPort(roomInfo.masterPort);
        gamePortVO.setGroundPort(roomInfo.groundPort);
        gamePortVO.setCavesPort(roomInfo.cavesPort);
        return gamePortVO;
    }

    public void saveGamePort(GamePortVO gamePortVO, String roomId) {
        RoomInfo roomInfo = roomInfoMapper.selectById(roomId);

        roomInfo.masterPort = gamePortVO.getMasterPort();
        roomInfo.groundPort = gamePortVO.getGroundPort();
        roomInfo.cavesPort = gamePortVO.getCavesPort();

        roomInfoMapper.updateById(roomInfo);
//        DBUtils.saveDataToFile();
    }


}
