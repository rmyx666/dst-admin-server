package com.tugos.dst.admin.service;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.config.I18nResourcesConfig;
import com.tugos.dst.admin.dao.RoomInfoMapper;
import com.tugos.dst.admin.dao.PlayerLogMapper;
import com.tugos.dst.admin.utils.DstConstant;
import com.tugos.dst.admin.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * @author qinming
 * @date 2020-11-16 23:03:32
 * <p> 玩家设置 </p>
 */
@Service
public class PlayerService {
    @Autowired
    private SystemService systemService;

    @Autowired
    PlayerLogMapper playerLogMapper;

    @Autowired
    DataService dataService;

    @Autowired
    RoomInfoMapper roomInfoMapper;

    /**
     * 读取游戏管理员列表
     */
    public List<String> getDstAdminList(String roomId) {
        String path = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_ADMIN_LIST_PATH;
        return FileUtils.readLineFile(path.replace("MyDediServer", roomId));
    }

    /**
     * 读取玩家黑名单列表
     */
    public List<String> getDstBlacklist(String roomId) {
        String path = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_PLAYER_BLOCK_LIST_PATH;
        return FileUtils.readLineFile(path.replace("MyDediServer", roomId));
    }

    /**
     * 读取玩家白名单列表
     */
    public List<String> getDstWhitelist(String roomId) {
        String path = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_PLAYER_WHITE_LIST_PATH;
        return FileUtils.readLineFile(path.replace("MyDediServer", roomId));
    }

    /**
     * 保存管理员
     */
    public ResultVO<String> saveAdminList(List<String> adminList, String roomId) throws Exception {
        if (!this.checkConfigIsExists(roomId)) {
            String path = DstConstant.ROOT_PATH + DstConstant.DST_USER_GAME_CONFG_PATH;
            return ResultVO.fail(I18nResourcesConfig.getMessage("tip.player.config.not.exist") + ":" + path.replace("MyDediServer", roomId));
        }
        String path = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_ADMIN_LIST_PATH;
        FileUtils.writeLineFile(path.replace("MyDediServer", roomId), adminList);
        return ResultVO.success();
    }

    /**
     * 保存黑名单
     */
    public ResultVO<String> saveBlackList(List<String> blackList, String roomId) throws Exception {
        if (!this.checkConfigIsExists(roomId)) {
            String path = DstConstant.ROOT_PATH + DstConstant.DST_USER_GAME_CONFG_PATH;
            return ResultVO.fail(I18nResourcesConfig.getMessage("tip.player.config.not.exist") + ":" + path.replace("MyDediServer", roomId));
        }
        String path = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_PLAYER_BLOCK_LIST_PATH;
        FileUtils.writeLineFile(path.replace("MyDediServer", roomId), blackList);
        return ResultVO.success();
    }

    /**
     * 保存黑名单
     */
    public ResultVO<String> saveWhiteList(List<String> whiteList, String roomId) throws Exception {
        if (!this.checkConfigIsExists(roomId)) {
            String path = DstConstant.ROOT_PATH + DstConstant.DST_USER_GAME_CONFG_PATH;
            return ResultVO.fail(I18nResourcesConfig.getMessage("tip.player.config.not.exist") + ":" + path.replace("MyDediServer", roomId));
        }
        String path = DstConstant.ROOT_PATH + DstConstant.SINGLE_SLASH + DstConstant.DST_PLAYER_WHITE_LIST_PATH;
        FileUtils.writeLineFile(path.replace("MyDediServer", roomId), whiteList);
        return ResultVO.success();
    }

    /**
     * 监测配置文件文件夹是否存在
     *
     * @return true存在
     */
    private boolean checkConfigIsExists(String roomId) {
        String filePath = DstConstant.ROOT_PATH + DstConstant.DST_USER_GAME_CONFG_PATH;
        File file = new File(filePath.replace("MyDediServer", roomId));
        return file.exists();
    }





}
