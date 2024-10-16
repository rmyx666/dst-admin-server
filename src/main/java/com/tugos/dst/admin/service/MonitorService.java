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
import java.text.SimpleDateFormat;
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

        // 查询数据库获取玩家日志
        QueryWrapper<PlayerLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("room_id", roomId)
                .between("create_time", startTime, endTime);

        List<PlayerLog> playerLogList = playerLogMapper.selectList(queryWrapper);

        // 生成并补充缺失的时间段
        List<OnlineTrendVo> onlineTrends;
        if ("minute".equalsIgnoreCase(type)) {
            onlineTrends = generateTrend(playerLogList, startTime, endTime, Calendar.MINUTE);
        } else if ("hour".equalsIgnoreCase(type)) {
            onlineTrends = generateTrend(playerLogList, startTime, endTime, Calendar.HOUR_OF_DAY);
        } else if ("day".equalsIgnoreCase(type)) {
            onlineTrends = generateTrend(playerLogList, startTime, endTime, Calendar.DAY_OF_MONTH);
        } else {
            onlineTrends = new ArrayList<>();
        }

        // 按时间排序
        onlineTrends.sort(Comparator.comparing(OnlineTrendVo::getTimePeriod));
        return onlineTrends;
    }

    // 生成时间段并进行数据处理
    private List<OnlineTrendVo> generateTrend(List<PlayerLog> playerLogList, Date startTime, Date endTime, int calendarField) {
        List<OnlineTrendVo> trends = new ArrayList<>();
        Map<String, Map<String, PlayerLog>> trendMap = new TreeMap<>();

        // 初始化时间段，并设置默认 count 为 0
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);

        SimpleDateFormat sdf;
        if (calendarField == Calendar.MINUTE) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        } else if (calendarField == Calendar.HOUR_OF_DAY) {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:00");
        } else {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
        }

        // 生成所有的时间点，默认 count 为 0
        while (!cal.getTime().after(endTime)) {
            String timePeriod = sdf.format(cal.getTime());
            trendMap.put(timePeriod, new HashMap<>()); // 每个时间点存放去重后的玩家记录
            cal.add(calendarField, 1);
        }

        // 处理日志数据，按时间段去重并保留 playerage 最大的记录
        for (PlayerLog log : playerLogList) {
            Calendar logCal = Calendar.getInstance();
            logCal.setTime(log.getCreateTime());

            if (calendarField == Calendar.MINUTE) {
                logCal.set(Calendar.SECOND, 0);
            } else if (calendarField == Calendar.HOUR_OF_DAY) {
                logCal.set(Calendar.MINUTE, 0);
                logCal.set(Calendar.SECOND, 0);
            } else if (calendarField == Calendar.DAY_OF_MONTH) {
                logCal.set(Calendar.HOUR_OF_DAY, 0);
                logCal.set(Calendar.MINUTE, 0);
                logCal.set(Calendar.SECOND, 0);
            }

            String timePeriod = sdf.format(logCal.getTime());

            // 去重逻辑：按 userId 排重，保留 playerage 最大的记录
            trendMap.computeIfPresent(timePeriod, (k, userLogMap) -> {
                userLogMap.merge(log.getUserId(), log, (existing, newEntry) ->
                        existing.getPlayerage() >= newEntry.getPlayerage() ? existing : newEntry);
                return userLogMap;
            });
        }

        // 转换为 List<OnlineTrendVo>
        for (Map.Entry<String, Map<String, PlayerLog>> entry : trendMap.entrySet()) {
            String timePeriod = entry.getKey();
            Map<String, PlayerLog> playerLogMap = entry.getValue();

            OnlineTrendVo trendVo = new OnlineTrendVo();
            trendVo.setTimePeriod(timePeriod);
            trendVo.setCount(playerLogMap.size()); // 玩家数量
            trendVo.setPlayerLogs(new ArrayList<>(playerLogMap.values())); // 去重后的玩家日志

            trends.add(trendVo);
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

// 转换为 List 并按 playerage 倒序排序
        List<PlayerLog> sortedPlayerLogs = uniquePlayerLogs.values().stream()
                .sorted(Comparator.comparing(PlayerLog::getPlayerage).reversed()) // 按 playerage 倒序排序
                .collect(Collectors.toList());
        return sortedPlayerLogs;
    }
}
