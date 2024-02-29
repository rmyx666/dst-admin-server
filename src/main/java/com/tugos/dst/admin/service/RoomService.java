package com.tugos.dst.admin.service;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.config.I18nResourcesConfig;
import com.tugos.dst.admin.utils.DstConfigData;
import com.tugos.dst.admin.utils.DstConfigRoomData;
import com.tugos.dst.admin.utils.DstConstant;
import com.tugos.dst.admin.utils.FileUtils;
import com.tugos.dst.admin.vo.RoomInfoVO;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qinming
 * @date 2020-11-16 23:03:32
 * <p> 玩家设置 </p>
 */
@Service
public class RoomService {


    public ResultVO<String> saveRoomInfos(DstConfigRoomData roomInfo) {

        if (DstConfigData.ROOM_INFO_MAP.containsKey(roomInfo.roomId)) {
            return ResultVO.fail("roomId重复");
        }


        //配置每天6点更新游戏
        roomInfo.SCHEDULE_UPDATE_MAP.put("06:00:00", 0);
        //每天6点，18点备份
        roomInfo.SCHEDULE_BACKUP_MAP.put("06:00:00", 0);
        roomInfo.SCHEDULE_BACKUP_MAP.put("18:00:00", 0);

        roomInfo.setNotStartMaster(false);
        roomInfo.setNotStartCaves(false);

        DstConfigData.ROOM_INFO_MAP.put(roomInfo.roomId, roomInfo);


        return ResultVO.success();
    }

    public List<RoomInfoVO> getRoomInfos() {
        List<DstConfigRoomData> roomInfoList = new ArrayList<>(DstConfigData.ROOM_INFO_MAP.values());

        List<RoomInfoVO> roomInfoVOS = new ArrayList<>();

        for (DstConfigRoomData dstConfigRoomData : roomInfoList) {
            RoomInfoVO roomInfoVO = new RoomInfoVO();
            BeanUtils.copyProperties(dstConfigRoomData, roomInfoVO);
            roomInfoVOS.add(roomInfoVO);
        }


        return roomInfoVOS;
    }
}
