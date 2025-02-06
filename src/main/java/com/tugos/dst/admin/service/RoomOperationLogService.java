package com.tugos.dst.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tugos.dst.admin.dao.RoomInfoMapper;
import com.tugos.dst.admin.dao.RoomOperationLogMapper;
import com.tugos.dst.admin.entity.PlayerLog;
import com.tugos.dst.admin.entity.RoomInfo;
import com.tugos.dst.admin.entity.RoomOperationLog;
import com.tugos.dst.admin.vo.GameSnapshotVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class RoomOperationLogService {

    @Autowired
    RoomOperationLogMapper roomOperationLogMapper;

    @Autowired
    RoomInfoMapper roomInfoMapper;

    @Autowired
    private BackupService backupService;

    @Autowired
    ShellService shellService;

    @Async
    public void saveRoomOperationLog() throws InterruptedException {
        List<RoomInfo> roomInfoList = roomInfoMapper.selectList(null);
        for (RoomInfo roomData : roomInfoList) {
            String roomId = roomData.getRoomId();
            String playDay = "0";
            GameSnapshotVO gameSnapshot = backupService.getGameSnapshot(roomId);
            if (gameSnapshot == null) {
                if (StringUtils.isNotBlank(gameSnapshot.getPlayDay())) {
                    playDay = gameSnapshot.getPlayDay();
                }
            }


            boolean masterStatus = shellService.getMasterStatus(roomId);
            boolean cavesStatus = shellService.getCavesStatus(roomId);

            RoomOperationLog build = RoomOperationLog.builder()
                    .createTime(new Date())
                    .roomId(roomId)
                    .playDay(playDay)
                    .masterStatus(masterStatus)
                    .cavesStatus(cavesStatus)
                    .build();

            // 将每次插入操作放到独立事务中
            saveRoomOperationLogInTransaction(build);
            Thread.sleep(1000); // 睡眠1秒
        }
    }

    @Transactional
    public void saveRoomOperationLogInTransaction(RoomOperationLog build) throws InterruptedException {
        roomOperationLogMapper.insert(build);
    }

    /**
     * @return void
     * @Title delRoomOperationLog
     * @Description 删除距现在超过一个月的数据
     * @author wgr
     * @date 2025/2/6 11:06
     */

    public void delRoomOperationLog() {
        // 获取当前时间
        Date currentTime = new Date();

        // 获取一个月前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        calendar.add(Calendar.MONTH, -1); // 当前时间减去一个月

        Date oneMonthAgo = calendar.getTime();

        // 构造查询条件，删除超过一个月的数据
        QueryWrapper<RoomOperationLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.lt("create_time", oneMonthAgo); // 小于一个月前的时间

        // 执行删除操作
        roomOperationLogMapper.delete(queryWrapper);
    }
}
