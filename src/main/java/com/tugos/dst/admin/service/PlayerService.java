package com.tugos.dst.admin.service;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.config.I18nResourcesConfig;
import com.tugos.dst.admin.dao.DstConfigRoomDataMapper;
import com.tugos.dst.admin.dao.PlayerLogMapper;
import com.tugos.dst.admin.entity.PlayerLog;
import com.tugos.dst.admin.enums.DstLogTypeEnum;
import com.tugos.dst.admin.entity.DstConfigRoomData;
import com.tugos.dst.admin.utils.DstConstant;
import com.tugos.dst.admin.utils.FileUtils;
import com.tugos.dst.admin.utils.ShellUtil;
import org.apache.commons.collections.CollectionUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    DstConfigRoomDataMapper dstConfigRoomDataMapper;

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


    /**
     * 解析日志获取玩家信息
     * [14:18:00]: playerlist 1621253438444 [0] KU_c1gvcIl4 [Host] 0
     * [14:18:00]: playerlist 1621253438444 [1] KU_***** nickname wendy 13
     *
     * @return ku_** 昵称 角色 生存时间
     */
    public List<String> getPlayerList(String roomId) throws Exception {
        String playerPrefix = "KU_";
        String host = "[Host]";
        String timeMillis = System.currentTimeMillis() + "";
        String cmd = DstConstant.MASTER_PLAYERAGE_CMD.replace("99999999", timeMillis);
        ShellUtil.runShell(cmd.replace("DST_MASTER", "Master_" + roomId));
        //睡眠一秒
        TimeUnit.SECONDS.sleep(1);
        List<String> dstLog = systemService.getDstLog(DstLogTypeEnum.MASTER_LOG.type, 100, roomId);
        List<String> playList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(dstLog)) {
            dstLog.forEach(e -> {
                if (e.contains(timeMillis)) {
                    if (e.contains(playerPrefix)) {
                        String tmp = e.substring(e.indexOf(playerPrefix)).replace("\t", "");
                        if (!tmp.contains(host)) {
                            playList.add(tmp);
                        }
                    }
                }
            });
        }
        return playList;
    }

    /**
     * @param roomId
     * @return java.util.List<com.tugos.dst.admin.entity.PlayerLog>
     * @Title getPlayerLog
     * @Description 获取玩家在线日志
     * @author wgr
     * @date 2024/10/12 17:19
     */
    private List<PlayerLog> getPlayerLog(String roomId) throws Exception {

        List<PlayerLog> playerLogList = new ArrayList<>();

        List<String> playerList = getPlayerList(roomId);

        for (String s : playerList) {
            String[] split = s.split(" ");
            PlayerLog playerLog = PlayerLog.builder()
                    .createTime(new Date())
                    .roomId(roomId)
                    .userId(split[0])
                    .name(split[1]).prefab(split[2])
                    .playerage(Integer.valueOf(split[3]))
                    .build();
            playerLogList.add(playerLog);
        }

        return playerLogList;
    }


    @Transactional
    void saveRoomPlayerLog(String roomId) throws Exception {
        List<PlayerLog> playerLog = getPlayerLog(roomId);
        for (PlayerLog log : playerLog) {
            playerLogMapper.insert(log);
        }

    }

    @Async
    public void savePlayerLog() throws Exception {
        //获取本地数据
        List<DstConfigRoomData> roomInfoList = dstConfigRoomDataMapper.selectList(null);


        for (DstConfigRoomData roomData : roomInfoList) {
            saveRoomPlayerLog(roomData.getRoomId());
        }
    }


}
