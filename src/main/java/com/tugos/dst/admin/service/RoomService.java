package com.tugos.dst.admin.service;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.config.I18nResourcesConfig;

import com.tugos.dst.admin.utils.DstConfigRoomData;
import com.tugos.dst.admin.utils.DstConstant;
import com.tugos.dst.admin.utils.FileUtils;
import com.tugos.dst.admin.vo.*;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinming
 * @date 2020-11-16 23:03:32
 * <p> 玩家设置 </p>
 */
@Log4j2
@Service
public class RoomService {

    @Autowired
    EhcacheDataService ehcacheDataService;

    @Autowired
    HomeService homeService;

    @Autowired
    BackupService backupService;

    @Autowired
    ShellService shellService;


    public ResultVO<String> saveRoomInfos(DstConfigRoomData roomInfo) {
        roomInfo.setRoomId("SERVER_" + roomInfo.getRoomId());

        Map<String, DstConfigRoomData> roomInfoMap = ehcacheDataService.getRoomInfoMap();
        if (roomInfoMap.containsKey(roomInfo.roomId)) {
            return ResultVO.fail("roomId重复");
        }


        //配置每天6点更新游戏
        roomInfo.SCHEDULE_UPDATE_MAP=new HashMap<>();
        roomInfo.SCHEDULE_UPDATE_MAP.put("06:00:00", 0);
        //每天6点，18点备份
        roomInfo.SCHEDULE_BACKUP_MAP=new HashMap<>();
        roomInfo.SCHEDULE_BACKUP_MAP.put("06:00:00", 0);
        roomInfo.SCHEDULE_BACKUP_MAP.put("18:00:00", 0);

        roomInfo.setNotStartMaster(false);
        roomInfo.setNotStartCaves(false);

        roomInfoMap.put(roomInfo.roomId, roomInfo);
        ehcacheDataService.updateRoomInfoMap(roomInfoMap);
        //创建新房间的文件夹
        String basePath = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_DOC_PATH + DstConstant.SINGLE_SLASH + roomInfo.getRoomId();
        FileUtils.mkdirs(basePath);


        return ResultVO.success();
    }

    public List<RoomInfoVO> getRoomInfos()  {
        List<DstConfigRoomData> roomInfoList = new ArrayList<>(ehcacheDataService.getRoomInfoMap().values());

        List<RoomInfoVO> roomInfoVOS = new ArrayList<>();

        for (DstConfigRoomData dstConfigRoomData : roomInfoList) {
            RoomInfoVO roomInfoVO = new RoomInfoVO();
            BeanUtils.copyProperties(dstConfigRoomData, roomInfoVO);


            DstServerInfoVO systemInfo = null;
            try {
                systemInfo = homeService.getSystemInfo(roomInfoVO.getRoomId());
                roomInfoVO.setMasterStatus(systemInfo.getMasterStatus());
                roomInfoVO.setCavesStatus(systemInfo.getCavesStatus());
                CpuVo cpuVo = new CpuVo();
                BeanUtils.copyProperties(systemInfo.getCpu(),cpuVo);
                roomInfoVO.setCpu(cpuVo);
                MemVo memVo = new MemVo();
                BeanUtils.copyProperties(systemInfo.getMem(),memVo);
                roomInfoVO.setMem(memVo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<String> playerList = null;
            try {
                playerList = shellService.getPlayerList(roomInfoVO.getRoomId());
                roomInfoVO.setNowPlayers(playerList.size());
            } catch (Exception e) {
                e.printStackTrace();
            }

            GameArchiveVO gameArchive = null;
            try {
                gameArchive = homeService.getGameArchive(roomInfoVO.getRoomId());
                roomInfoVO.setClusterName(gameArchive.getClusterName());
                roomInfoVO.setMaxPlayers(gameArchive.getMaxPlayers());
                roomInfoVO.setPlayDay(gameArchive.getPlayDay());
                roomInfoVO.setSeason(gameArchive.getSeason());
                roomInfoVO.setTotalModNum(gameArchive.getTotalModNum());

            } catch (Exception e) {
                e.printStackTrace();
            }


            roomInfoVOS.add(roomInfoVO);
        }


        return roomInfoVOS;
    }

    public ResultVO<String> delRoomInfos(String roomId) {

        Map<String, DstConfigRoomData> roomInfoMap = ehcacheDataService.getRoomInfoMap();
        roomInfoMap.remove(roomId);
        ehcacheDataService.updateRoomInfoMap(roomInfoMap);
        //删除房间的文件夹
        backupService.delRoomDir(roomId);
        return ResultVO.success();
    }

    public ResultVO<String> updateRoomInfos(DstConfigRoomData roomInfo) {
        Map<String, DstConfigRoomData> roomInfoMap = ehcacheDataService.getRoomInfoMap();
        DstConfigRoomData dstConfigRoomData = roomInfoMap.get(roomInfo.getRoomId());
        dstConfigRoomData.setRoomName(roomInfo.getRoomName());
        roomInfoMap.put(roomInfo.getRoomId(),dstConfigRoomData);
        ehcacheDataService.updateRoomInfoMap(roomInfoMap);
        return ResultVO.success();
    }
}
