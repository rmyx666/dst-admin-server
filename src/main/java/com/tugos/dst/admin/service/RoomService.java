package com.tugos.dst.admin.service;

import com.tugos.dst.admin.common.ResultVO;

import com.tugos.dst.admin.utils.DstConfigRoomData;
import com.tugos.dst.admin.utils.DstConstant;
import com.tugos.dst.admin.utils.FileUtils;
import com.tugos.dst.admin.vo.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author qinming
 * @date 2020-11-16 23:03:32
 * <p> 玩家设置 </p>
 */
@Log4j2
@Service
public class RoomService {

    @Autowired
    DataService dataService;

    @Autowired
    HomeService homeService;

    @Autowired
    BackupService backupService;

    @Autowired
    ShellService shellService;

    @Autowired
    ServerService serverService;


    public ResultVO<String> saveRoomInfos(DstConfigRoomData roomInfo) {
        roomInfo.setRoomId("SERVER_" + roomInfo.getRoomId());

        Map<String, DstConfigRoomData> roomInfoMap = dataService.getRoomInfoMap();
        if (roomInfoMap.containsKey(roomInfo.roomId)) {
            return ResultVO.fail("roomId重复");
        }


        //配置每天6点更新游戏
        roomInfo.SCHEDULE_UPDATE_MAP = new HashMap<>();
        roomInfo.SCHEDULE_UPDATE_MAP.put("06:00:00", 0);
        //每天6点，18点备份
        roomInfo.SCHEDULE_BACKUP_MAP = new HashMap<>();
        roomInfo.SCHEDULE_BACKUP_MAP.put("06:00:00", 0);
        roomInfo.SCHEDULE_BACKUP_MAP.put("18:00:00", 0);

        roomInfo.setNotStartMaster(false);
        roomInfo.setNotStartCaves(false);

        roomInfoMap.put(roomInfo.roomId, roomInfo);
        dataService.updateRoomInfoMap(roomInfoMap);
        //创建新房间的文件夹
        String basePath = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_DOC_PATH + DstConstant.SINGLE_SLASH + roomInfo.getRoomId();
        FileUtils.mkdirs(basePath);


        return ResultVO.success();
    }

    public List<RoomInfoVO> getServerRoomInfos() throws Exception {


        List<RoomInfoVO> roomInfoVOS = new ArrayList<>();

        //获取配置服务器的房间列表
        List<RoomInfoVO> serverInfoList = serverService.getServerInfoList();
        roomInfoVOS.addAll(serverInfoList);


        return roomInfoVOS;
    }

    public List<RoomInfoVO> getLocalRoomInfos() {
        //获取本地数据
        List<DstConfigRoomData> roomInfoList = new ArrayList<>(dataService.getRoomInfoMap().values());

        List<RoomInfoVO> roomInfoVOS = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(10); // 创建一个具有固定线程数的线程池
        List<Future<?>> futures = new ArrayList<>();

        for (DstConfigRoomData dstConfigRoomData : roomInfoList) {
            Future<?> future = executorService.submit(() -> {
                RoomInfoVO roomInfoVO = new RoomInfoVO();
                BeanUtils.copyProperties(dstConfigRoomData, roomInfoVO);

                try {
                    DstServerInfoVO dstInfo = homeService.getDstInfo(roomInfoVO.getRoomId());
                    roomInfoVO.setMasterStatus(dstInfo.getMasterStatus());
                    roomInfoVO.setCavesStatus(dstInfo.getCavesStatus());

                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    List<String> playerList = shellService.getPlayerList(roomInfoVO.getRoomId());
                    roomInfoVO.setNowPlayers(playerList.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    GameArchiveVO gameArchive = homeService.getGameArchive(roomInfoVO.getRoomId());
                    roomInfoVO.setClusterName(gameArchive.getClusterName());
                    roomInfoVO.setMaxPlayers(gameArchive.getMaxPlayers());
                    roomInfoVO.setPlayDay(gameArchive.getPlayDay());
                    roomInfoVO.setSeason(gameArchive.getSeason());
                    roomInfoVO.setTotalModNum(gameArchive.getTotalModNum());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                roomInfoVO.setServerName("本机");
                roomInfoVO.setServerIp("127.0.0.1");
                synchronized (roomInfoVOS) {
                    roomInfoVOS.add(roomInfoVO);
                }
            });

            futures.add(future);
        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();


        //排序
        roomInfoVOS.sort(Comparator.comparing(RoomInfoVO::getRoomId));


        return roomInfoVOS;
    }

    public List<RoomInfoVO> getLocalRoomInfosWithHardware() throws Exception {
        //获取本地数据
        List<DstConfigRoomData> roomInfoList = new ArrayList<>(dataService.getRoomInfoMap().values());

        List<RoomInfoVO> roomInfoVOS = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(10); // 创建一个具有固定线程数的线程池
        List<Future<?>> futures = new ArrayList<>();

        for (DstConfigRoomData dstConfigRoomData : roomInfoList) {
            Future<?> future = executorService.submit(() -> {
                RoomInfoVO roomInfoVO = new RoomInfoVO();
                BeanUtils.copyProperties(dstConfigRoomData, roomInfoVO);

                try {
                    DstServerInfoVO systemInfo = homeService.getSystemInfo(roomInfoVO.getRoomId());
                    roomInfoVO.setMasterStatus(systemInfo.getMasterStatus());
                    roomInfoVO.setCavesStatus(systemInfo.getCavesStatus());
                    CpuVo cpuVo = new CpuVo();
                    BeanUtils.copyProperties(systemInfo.getCpu(), cpuVo);
                    roomInfoVO.setCpu(cpuVo);
                    MemVo memVo = new MemVo();
                    BeanUtils.copyProperties(systemInfo.getMem(), memVo);
                    roomInfoVO.setMem(memVo);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    List<String> playerList = shellService.getPlayerList(roomInfoVO.getRoomId());
                    roomInfoVO.setNowPlayers(playerList.size());
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    GameArchiveVO gameArchive = homeService.getGameArchive(roomInfoVO.getRoomId());
                    roomInfoVO.setClusterName(gameArchive.getClusterName());
                    roomInfoVO.setMaxPlayers(gameArchive.getMaxPlayers());
                    roomInfoVO.setPlayDay(gameArchive.getPlayDay());
                    roomInfoVO.setSeason(gameArchive.getSeason());
                    roomInfoVO.setTotalModNum(gameArchive.getTotalModNum());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                roomInfoVO.setServerName("本机");
                roomInfoVO.setServerIp("127.0.0.1");
                synchronized (roomInfoVOS) {
                    roomInfoVOS.add(roomInfoVO);
                }
            });

            futures.add(future);
        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();


        //排序
        roomInfoVOS.sort(Comparator.comparing(RoomInfoVO::getRoomId));


        return roomInfoVOS;
    }

    public ResultVO<String> delRoomInfos(String roomId) {

        Map<String, DstConfigRoomData> roomInfoMap = dataService.getRoomInfoMap();
        roomInfoMap.remove(roomId);
        dataService.updateRoomInfoMap(roomInfoMap);
        //删除房间的文件夹
        backupService.delRoomDir(roomId);
        return ResultVO.success();
    }

    public ResultVO<String> updateRoomInfos(DstConfigRoomData roomInfo) {
        Map<String, DstConfigRoomData> roomInfoMap = dataService.getRoomInfoMap();
        DstConfigRoomData dstConfigRoomData = roomInfoMap.get(roomInfo.getRoomId());
        dstConfigRoomData.setRoomName(roomInfo.getRoomName());
        dstConfigRoomData.setMasterPort(roomInfo.getMasterPort());
        dstConfigRoomData.setCavesPort(roomInfo.getCavesPort());
        dstConfigRoomData.setGroundPort(roomInfo.getGroundPort());
        roomInfoMap.put(roomInfo.getRoomId(), dstConfigRoomData);
        dataService.updateRoomInfoMap(roomInfoMap);
        return ResultVO.success();
    }

    /**
     * 更新房间在自动更新时，是否自动启动地面或者洞穴的标志位
     *
     * @param roomId
     */
    public void updateStartFlag(String roomId, Boolean startMsater, Boolean startCaves) {
        Map<String, DstConfigRoomData> roomInfoMap = dataService.getRoomInfoMap();
        DstConfigRoomData dstConfigRoomData = roomInfoMap.get(roomId);

        if (startMsater != null) {
            dstConfigRoomData.notStartMaster = !startMsater;
        }
        if (startCaves != null) {
            dstConfigRoomData.notStartCaves = !startCaves;
        }

        roomInfoMap.put(roomId, dstConfigRoomData);
        dataService.updateRoomInfoMap(roomInfoMap);
    }
}
