package com.tugos.dst.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.dao.PlayerLogMapper;
import com.tugos.dst.admin.entity.PlayerLog;
import com.tugos.dst.admin.param.PlayerOnlineParam;
import com.tugos.dst.admin.vo.OnlineTrendVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MonitorService {

    @Autowired
    PlayerLogMapper playerLogMapper;


    public List<OnlineTrendVo> getPlayerOnlineTrend(PlayerOnlineParam param) {

        String roomId = param.getRoomId();
        Date startTime = param.getStartTime();
        Date endTime = param.getEndTime();
        String type = param.getType();


        QueryWrapper<PlayerLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId)
                .between("create_time", startTime, endTime); // 获取玩家生存天数大于指定值的日志

        List<PlayerLog> playerLogList = playerLogMapper.selectList(queryWrapper);

        // 统计玩家在线情况
        List<OnlineTrendVo> onlineTrends = new ArrayList<>();

        // 根据 type 进行统计
        if ("minute".equalsIgnoreCase(type)) {
            onlineTrends = getTrendByMinute(playerLogList);
        } else if ("hour".equalsIgnoreCase(type)) {
            onlineTrends = getTrendByHour(playerLogList);
        } else if ("day".equalsIgnoreCase(type)) {
            onlineTrends = getTrendByDay(playerLogList);
        }

        return onlineTrends;
    }

    // 按分钟统计
    private List<OnlineTrendVo> getTrendByMinute(List<PlayerLog> playerLogList) {
        return generateTrend(playerLogList, "minute");
    }

    // 按小时统计
    private List<OnlineTrendVo> getTrendByHour(List<PlayerLog> playerLogList) {
        return generateTrend(playerLogList, "hour");
    }

    // 按天统计
    private List<OnlineTrendVo> getTrendByDay(List<PlayerLog> playerLogList) {
        return generateTrend(playerLogList, "day");
    }

    // 生成趋势数据
    private List<OnlineTrendVo> generateTrend(List<PlayerLog> playerLogList, String type) {
        Map<String, Set<PlayerLog>> trendMap = new HashMap<>();

        for (PlayerLog log : playerLogList) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(log.getCreateTime());

            // 按不同粒度设定时间段格式
            if ("minute".equalsIgnoreCase(type)) {
                cal.set(Calendar.SECOND, 0); // 以分钟为粒度
            } else if ("hour".equalsIgnoreCase(type)) {
                cal.set(Calendar.MINUTE, 0); // 以小时为粒度
            } else if ("day".equalsIgnoreCase(type)) {
                cal.set(Calendar.HOUR_OF_DAY, 0); // 以天为粒度
            }

            String timePeriod = String.format("%tY-%<tm-%<td %<tH:%<tM", cal);

            // 创建时间段集合并去重（通过 userId ）

            trendMap.computeIfAbsent(timePeriod, k -> new TreeSet<>(Comparator.comparing(PlayerLog::getUserId))).add(log);
        }

        // 将去重后的数据转换为 List<OnlineTrend>
        List<OnlineTrendVo> trends = new ArrayList<>();
        for (Map.Entry<String, Set<PlayerLog>> entry : trendMap.entrySet()) {
            String timePeriod = entry.getKey();
            Set<PlayerLog> playerLogSet = entry.getValue();

            // 去重后的玩家列表
            List<PlayerLog> distinctPlayerLogs = new ArrayList<>(playerLogSet);

            // 将去重的结果加入 OnlineTrend 对象中
            OnlineTrendVo trend = new OnlineTrendVo(timePeriod, distinctPlayerLogs.size(), distinctPlayerLogs);
            trends.add(trend);
        }

        return trends;
    }

    public List<PlayerLog> getPlayerOnlineAge(PlayerOnlineParam param) {

        String roomId = param.getRoomId();

        // 获取当前时间和一个月前的时间
        Date now = new Date();
        Date oneMonthAgo = Date.from(now.toInstant().minusSeconds(30L * 24 * 60 * 60)); // 30天前

        // 构建查询条件
        QueryWrapper<PlayerLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId)
                .between("create_time", oneMonthAgo, now);

        // 获取玩家日志
        List<PlayerLog> playerLogList = playerLogMapper.selectList(queryWrapper);

        // 根据 userId 排重，并保留 playerage 最大的记录
        Map<String, PlayerLog> uniquePlayerLogs = playerLogList.stream()
                .collect(Collectors.toMap(
                        PlayerLog::getUserId, // key: userId
                        playerLog -> playerLog, // value: PlayerLog
                        (existing, newEntry) -> existing.getPlayerage() >= newEntry.getPlayerage() ? existing : newEntry // 保留 playerage 最大的
                ));

        // 转换为 List 并返回
        List<PlayerLog> collect = uniquePlayerLogs.values().stream().collect(Collectors.toList());
        return collect;
    }
}
