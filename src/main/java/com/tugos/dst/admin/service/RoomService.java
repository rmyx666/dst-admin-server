package com.tugos.dst.admin.service;

import com.tugos.dst.admin.common.ResultVO;

import com.tugos.dst.admin.dao.RoomInfoMapper;
import com.tugos.dst.admin.entity.RoomInfo;
import com.tugos.dst.admin.enums.StartTypeEnum;
import com.tugos.dst.admin.enums.StopTypeEnum;
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
    @Autowired
    RoomInfoMapper roomInfoMapper;

    public ResultVO<String> saveRoomInfos(RoomInfo roomInfo) {
        roomInfo.setRoomId("SERVER_" + roomInfo.getRoomId());

        List<RoomInfo> roomData = roomInfoMapper.selectList(null);
        if (roomData.stream().anyMatch(x -> x.getRoomId().equals(roomInfo.roomId))) {
            return ResultVO.fail("roomId重复");
        }


        //一小时更新一次mod
        roomInfo.scheduleUpdateModMap = new TreeMap<>();
        roomInfo.scheduleUpdateModMap.put("01:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("02:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("03:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("04:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("05:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("06:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("07:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("08:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("09:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("10:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("11:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("12:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("13:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("14:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("15:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("16:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("17:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("18:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("19:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("20:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("21:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("22:00:00", 0);
        roomInfo.scheduleUpdateModMap.put("23:00:00", 0);
        //每天6点，18点备份
        roomInfo.scheduleBackupMap = new TreeMap<>();
        roomInfo.scheduleBackupMap.put("06:00:00", 0);
        roomInfo.scheduleBackupMap.put("18:00:00", 0);
        //六点更新服务器
        roomInfo.scheduleBackupMap = new TreeMap<>();
        roomInfo.scheduleUpdateServerMap.put("06:00:00", 0);

        roomInfo.setAutoStartMaster(true);
        roomInfo.setAutoStartCaves(true);
        roomInfo.setAutoRegenerate(false);

        //默认自动更新开启
        roomInfo.setSmartUpdateMod(true);
        roomInfo.setSmartUpdateServer(true);

        roomInfoMapper.insert(roomInfo);
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
        List<RoomInfo> roomInfoList = roomInfoMapper.selectList(null);

        List<RoomInfoVO> roomInfoVOS = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(10); // 创建一个具有固定线程数的线程池
        List<Future<?>> futures = new ArrayList<>();

        for (RoomInfo roomInfo : roomInfoList) {
            Future<?> future = executorService.submit(() -> {
                RoomInfoVO roomInfoVO = new RoomInfoVO();
                BeanUtils.copyProperties(roomInfo, roomInfoVO);

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
        List<RoomInfo> roomInfoList = roomInfoMapper.selectList(null);

        List<RoomInfoVO> roomInfoVOS = new ArrayList<>();

        ExecutorService executorService = Executors.newFixedThreadPool(10); // 创建一个具有固定线程数的线程池
        List<Future<?>> futures = new ArrayList<>();

        for (RoomInfo roomInfo : roomInfoList) {
            Future<?> future = executorService.submit(() -> {
                RoomInfoVO roomInfoVO = new RoomInfoVO();
                BeanUtils.copyProperties(roomInfo, roomInfoVO);

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
        //删除房间信息
        roomInfoMapper.deleteById(roomId);

        //停止房间 删除房间文件夹 删除玩家日志
        homeService.delRoomPath(roomId);

        return ResultVO.success();
    }

    public ResultVO<String> updateRoomInfos(RoomInfo roomInfo) {
        RoomInfo dstConfigRoomData = roomInfoMapper.selectById(roomInfo.roomId);
        dstConfigRoomData.setRoomName(roomInfo.getRoomName());
        dstConfigRoomData.setMasterPort(roomInfo.getMasterPort());
        dstConfigRoomData.setCavesPort(roomInfo.getCavesPort());
        dstConfigRoomData.setGroundPort(roomInfo.getGroundPort());

        roomInfoMapper.updateById(roomInfo);
        return ResultVO.success();
    }

    /**
     * 更新房间在自动更新时，是否自动启动地面或者洞穴的标志位
     *
     * @param roomId
     */
    public void updateStartFlag(Boolean startOrStop, Integer type, String roomId) {
        RoomInfo roomInfo = roomInfoMapper.selectById(roomId);

        Boolean startMsater = null;
        Boolean startCaves = null;
        if (startOrStop) {
            StartTypeEnum typeEnum = StartTypeEnum.get(type);
            Objects.requireNonNull(typeEnum);
            switch (typeEnum) {
                case START_ALL:
                    startMsater = true;
                    startCaves = true;
                    break;
                case START_MASTER:
                    startMsater = true;
                    break;
                case START_CAVES:
                    startCaves = true;
                    break;
                default:
            }
        }else {
            StopTypeEnum typeEnum = StopTypeEnum.get(type);
            Objects.requireNonNull(typeEnum);
            switch (typeEnum) {
                case STOP_ALL:
                    startMsater = false;
                    startCaves = false;
                    break;
                case STOP_MASTER:
                    startMsater = false;
                    break;
                case STOP_CAVES:
                    startCaves = false;
                    break;
                default:
            }
        }


        if (startMsater != null) {
            roomInfo.autoStartMaster = startMsater;
        }
        if (startCaves != null) {
            roomInfo.autoStartCaves = startCaves;
        }

        roomInfoMapper.updateById(roomInfo);
    }
}
