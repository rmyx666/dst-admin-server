package com.tugos.dst.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.config.I18nResourcesConfig;
import com.tugos.dst.admin.dao.DstConfigRoomDataMapper;
import com.tugos.dst.admin.dao.PlayerLogMapper;
import com.tugos.dst.admin.entity.PlayerLog;
import com.tugos.dst.admin.entity.RoomInfo;
import com.tugos.dst.admin.enums.DstLogTypeEnum;
import com.tugos.dst.admin.utils.DstConstant;
import com.tugos.dst.admin.utils.FileUtils;
import com.tugos.dst.admin.utils.ShellUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author wgr
 * @Title 处理玩家日志
 * @Description
 * @date 2024/10/25 16:47
 */
@Service
public class PlayerLogService {
    @Autowired
    private SystemService systemService;

    @Autowired
    PlayerLogMapper playerLogMapper;

    @Autowired
    DataService dataService;

    @Autowired
    DstConfigRoomDataMapper dstConfigRoomDataMapper;


    /**
     * 解析日志获取玩家信息
     * [14:18:00]: playerlist 1621253438444 [0] KU_c1gvcIl4#split#[Host]#split#0
     * [14:18:00]: playerlist 1621253438444 [1] KU_*****#split#nickname#split#wendy#split#13
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
            String[] split = s.split("#split#");
            if (split.length == 4) {
                PlayerLog playerLog = PlayerLog.builder()
                        .createTime(new Date())
                        .roomId(roomId)
                        .userId(split[0])
                        .name(split[1]).prefab(split[2])
                        .playerage(Integer.valueOf(split[3]))
                        .build();
                playerLogList.add(playerLog);
            }
        }

        return playerLogList;
    }

    /**
     * @return java.util.Map<java.lang.String, java.util.List < com.tugos.dst.admin.entity.PlayerLog>>
     * @Title getAllPlayerLog
     * @Description 获取当前时间全部房间的玩家列表
     * @author wgr
     * @date 2024/10/25 15:55
     */
    public Map<RoomInfo, List<PlayerLog>> getAllPlayerLog() throws Exception {
        Map<RoomInfo, List<PlayerLog>> result = new HashMap<>();
        List<RoomInfo> roomInfoList = dstConfigRoomDataMapper.selectList(null);
        for (RoomInfo roomData : roomInfoList) {
            List<PlayerLog> playerLog = getPlayerLog(roomData.getRoomId());
            result.put(roomData, playerLog);
        }
        return result;
    }


    @Async
    @Transactional
    public void savePlayerLog(Map<RoomInfo, List<PlayerLog>> playerLogMap) throws Exception {

        for (List<PlayerLog> value : playerLogMap.values()) {
            for (PlayerLog playerLog : value) {
                playerLogMapper.insert(playerLog);
            }
        }
    }


    /**
     * @param roomId
     * @return void
     * @Title delRoomPlayerLog
     * @Description 删除roomId对应的日志文件
     * @author wgr
     * @date 2024/10/18 13:52
     */


    public void delRoomPlayerLog(String roomId) {
        // 查询数据库获取玩家日志
        QueryWrapper<PlayerLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId);
        playerLogMapper.delete(queryWrapper);
    }

}
