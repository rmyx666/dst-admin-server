package com.tugos.dst.admin.controller;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.entity.PlayerLog;
import com.tugos.dst.admin.param.PlayerOnlineParam;
import com.tugos.dst.admin.service.MonitorService;
import com.tugos.dst.admin.service.PlayerService;
import com.tugos.dst.admin.vo.OnlineTrendVo;
import com.tugos.dst.admin.vo.RoomOperationTrendVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/monitor")
@Slf4j
@Controller
public class MoniterController {


    @Autowired
    private MonitorService monitorService;

    @GetMapping("/index")
    @RequiresAuthentication
    public String index() {
        log.info("进入玩家监控页面");
        return "/monitor/index";
    }

    @PostMapping("/getPlayerOnlineTrend")
    @RequiresAuthentication
    @ResponseBody
    public ResultVO<List<OnlineTrendVo>> getPlayerOnlineTrend(@RequestBody PlayerOnlineParam param) {
        log.info("获取玩家在线情况趋势图");
        return ResultVO.data(monitorService.getPlayerOnlineTrend(param));
    }

    @PostMapping("/getPlayerOnlineAge")
    @RequiresAuthentication
    @ResponseBody
    public ResultVO<List<PlayerLog>> getPlayerOnlineAge(@RequestBody PlayerOnlineParam param) {
        log.info("获取玩家在线时间");
        return ResultVO.data(monitorService.getPlayerOnlineAge(param));
    }

    @PostMapping("/getRoomOperationTrend")
    @RequiresAuthentication
    @ResponseBody
    public ResultVO<List<RoomOperationTrendVo>> getRoomOperationTrend(@RequestBody PlayerOnlineParam param) {
        log.info("获取玩家在线情况趋势图");
        return ResultVO.data(monitorService.getRoomOperationTrend(param));
    }
}
