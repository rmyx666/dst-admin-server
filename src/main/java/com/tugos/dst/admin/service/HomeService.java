package com.tugos.dst.admin.service;


import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.config.I18nResourcesConfig;
import com.tugos.dst.admin.dao.PlayerLogMapper;
import com.tugos.dst.admin.entity.PlayerLog;
import com.tugos.dst.admin.entity.RoomInfo;
import com.tugos.dst.admin.systementity.Server;
import com.tugos.dst.admin.enums.StartTypeEnum;
import com.tugos.dst.admin.enums.StopTypeEnum;
import com.tugos.dst.admin.utils.DstConstant;
import com.tugos.dst.admin.utils.ModFileUtil;
import com.tugos.dst.admin.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author qinming
 * @date 2020-05-17
 * <p> 管理游戏服务 </p>
 */
@Service
@Slf4j
public class HomeService {

    private ShellService shellService;
    private BackupService backupService;
    private SettingService settingService;
    @Autowired
    private RoomService roomService;

    @Autowired
    PlayerLogService playerLogService;

    /**
     * 启动服务器进程
     *
     * @param type 0 启动所有 1 启动地面 2 启动洞穴
     */
    public ResultVO<String> start(Integer type, String roomId) {
        if (!this.checkIsInstallDst()) {
            //未安装dst
            return ResultVO.fail(I18nResourcesConfig.getMessage("tip.home.start.error"));
        }
        StartTypeEnum typeEnum = StartTypeEnum.get(type);
        Objects.requireNonNull(typeEnum);
        switch (typeEnum) {
            case START_ALL:
                //启动所有 先停止所有，在启动
                shellService.stopMaster(roomId);
                shellService.stopCaves(roomId);
                shellService.startMaster(roomId);
                shellService.startCaves(roomId);
                break;
            case START_MASTER:
                //启动地面
                shellService.stopMaster(roomId);
                shellService.startMaster(roomId);
                break;
            case START_CAVES:
                //启动洞穴
                shellService.stopCaves(roomId);
                shellService.startCaves(roomId);
                break;
            default:
        }
        //清理screen的无效作业
        shellService.clearScreen();
        return ResultVO.success();
    }

    /**
     * 直接停止服务
     */
    public void stopServer(String roomId) {
        shellService.stopMaster(roomId);
        shellService.stopCaves(roomId);
    }

    /**
     * 停止服务器进程 优雅关闭
     *
     * @param type 0 停止所有 1 停止地面 2 停止洞穴
     */
    public ResultVO<String> stop(Integer type, String roomId) {
        StopTypeEnum typeEnum = StopTypeEnum.get(type);
        Objects.requireNonNull(typeEnum);
        switch (typeEnum) {
            case STOP_ALL:
                //停止所有,优雅关闭，10秒还未关闭强制关闭
                shellService.elegantShutdownMaster(roomId);
                shellService.elegantShutdownCaves(roomId);
                break;
            case STOP_MASTER:
                //停止地面 优雅关闭，10秒还未关闭强制关闭
                shellService.elegantShutdownMaster(roomId);
                break;
            case STOP_CAVES:
                //停止洞穴 优雅关闭，10秒还未关闭强制关闭
                shellService.elegantShutdownCaves(roomId);
                break;
            default:
        }
        return ResultVO.success();
    }

    /**
     * @param roomInfo
     * @return com.tugos.dst.admin.common.ResultVO<java.lang.String>
     * @Title start
     * @Description 根据roominfo里的自动启动配置，启动房间
     * @author wgr
     * @date 2024/10/25 17:58
     */
    public ResultVO<String> start(RoomInfo roomInfo) {
        boolean autoStartMaster = roomInfo.autoStartMaster != null ? roomInfo.autoStartMaster : true;
        boolean autoStartCaves = roomInfo.autoStartCaves != null ? roomInfo.autoStartCaves : true;
        if (autoStartMaster && autoStartCaves) {
            //全启动
            start(StartTypeEnum.START_ALL.type, roomInfo.roomId);
        }
        if (!autoStartMaster && autoStartCaves) {
            //不启动地面
            start(StartTypeEnum.START_CAVES.type, roomInfo.roomId);
        }
        if (autoStartMaster && !autoStartCaves) {
            //不启动洞穴
            start(StartTypeEnum.START_MASTER.type, roomInfo.roomId);
        }
        return ResultVO.success();
    }

    /**
     * @param roomInfo
     * @return com.tugos.dst.admin.common.ResultVO<java.lang.String>
     * @Title stop
     * @Description 根据roominfo关闭所有
     * @author wgr
     * @date 2024/10/25 18:00
     */
    public ResultVO<String> stop(RoomInfo roomInfo) {
        stop(0, roomInfo.roomId);
        return ResultVO.success();
    }

    /**
     * 校验服务器是否已经安装了dst，检查启动程序dontstarve_dedicated_server_nullrenderer 是否存在
     *
     * @return true 安装了
     */
    private boolean checkIsInstallDst() {
        String startProgram = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.START_DST_BIN_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_START_PROGRAM;
        File masterFile = new File(startProgram);
        if (masterFile.exists()) {
            return true;
        } else {
            log.warn("未找到启动程序：{}", startProgram);
            return false;
        }
    }


    /**
     * 获取服务器的信息
     * 包括饥荒状态和硬件信息
     */
    public DstServerInfoVO getSystemInfo(String roomId) throws Exception {
        DstServerInfoVO data = new DstServerInfoVO();
        //获取硬件信息
        Server server = new Server();
        server.copyTo();
        data.setCpu(server.getCpu());
        data.setMem(server.getMem());
        //饥荒状态
        data.setMasterStatus(shellService.getMasterStatus(roomId));
        data.setCavesStatus(shellService.getCavesStatus(roomId));
        //备份的文件
        List<String> backupList = new ArrayList<>();
        List<BackupFileVO> list = backupService.getBackupFileInfo(roomId);
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(e -> backupList.add(e.getFileName()));
        }
        data.setBackupList(backupList);
        return data;
    }

    /**
     * 硬件信息
     */
    public DstServerInfoVO getHardwareInfo() throws Exception {
        DstServerInfoVO data = new DstServerInfoVO();
        //获取硬件信息
        Server server = new Server();
        server.copyTo();
        data.setCpu(server.getCpu());
        data.setMem(server.getMem());
        return data;
    }

    /**
     * 饥荒状态
     */
    public DstServerInfoVO getDstInfo(String roomId) throws Exception {
        DstServerInfoVO data = new DstServerInfoVO();
        //饥荒状态
        data.setMasterStatus(shellService.getMasterStatus(roomId));
        data.setCavesStatus(shellService.getCavesStatus(roomId));
        return data;
    }

    /**
     * 更新游戏 需要停止地面和洞穴进程
     */
    public ResultVO<String> updateGame(String roomId) {
        if (!this.checkIsInstallDst()) {
            //未安装dst
            return ResultVO.fail(I18nResourcesConfig.getMessage("tip.home.start.error"));
        }
        shellService.updateGame(roomId);
        return ResultVO.success();
    }


    /**
     * 清理地面和洞穴游戏进度，需要停止服务
     */
    public void delRecord(String roomId) {
        shellService.delCavesRecord(roomId);
        shellService.delMasterRecord(roomId);

        playerLogService.delRoomPlayerLog(roomId);
    }

    /**
     * 删除room文件夹
     */
    public void delRoomPath(String roomId) {
        this.stopServer(roomId);
        //删除房间的文件夹
        backupService.delRoomDir(roomId);

        //删除房间用户日志
        playerLogService.delRoomPlayerLog(roomId);
    }


    /**
     * 删除指定世界的存档
     */
    public void onlyDelSave(String roomId) {
        shellService.delCavesRecord(roomId);
    }

    /**
     * 解析存档信息 包括天数，季节等信息
     *
     * @return 存档信息
     */
    public GameArchiveVO getGameArchive(String roomId) throws Exception {
        GameArchiveVO gameArchiveVO = new GameArchiveVO();
        GameConfigVO config = settingService.getConfig(roomId);
        if (config != null) {
            gameArchiveVO.setModContent(config.getModData());
            BeanUtils.copyProperties(config, gameArchiveVO);
            //填充MOD信息
            List<String> modNoList = ModFileUtil.findModNo(config.getModData());
            gameArchiveVO.setTotalModNum(modNoList.size());
            gameArchiveVO.setModNos(modNoList);
        }
        gameArchiveVO.setMaxPlayers(gameArchiveVO.getMaxPlayers());
        GameSnapshotVO gameSnapshot = backupService.getGameSnapshot(roomId);
        if (gameSnapshot != null) {
            gameArchiveVO.setPlayDay(gameSnapshot.getPlayDay());
            gameArchiveVO.setSeason(gameSnapshot.getSeasonChinese());
        } else {
            gameArchiveVO.setPlayDay(I18nResourcesConfig.getMessage("tip.game.Archive.unknown.season"));
            gameArchiveVO.setSeason(I18nResourcesConfig.getMessage("tip.game.Archive.unknown.playDay"));
        }
        return gameArchiveVO;
    }

    @Autowired
    public void setShellService(ShellService shellService) {
        this.shellService = shellService;
    }

    @Autowired
    public void setBackupService(BackupService backupService) {
        this.backupService = backupService;
    }

    @Autowired
    public void setSettingService(SettingService settingService) {
        this.settingService = settingService;
    }
}
