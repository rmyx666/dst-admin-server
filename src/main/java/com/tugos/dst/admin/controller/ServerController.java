package com.tugos.dst.admin.controller;


import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.service.ServerService;
import com.tugos.dst.admin.vo.RoomInfoVO;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 获取远程配置的服务器的房间信息
 */
@Controller
@RequestMapping("/server")
public class ServerController {

    @Autowired
    ServerService serverService;

    @GetMapping("/index")
    @RequiresAuthentication
    public String index() {
        return "/server/index";
    }


    @ApiOperation(value = "获取房间信息", notes = "获取所有房间的信息 概览展示房间id 房间名称 服务器名称 季节 天数 在线情况 cpu 内存 地面和洞穴是否启动 三个端口号")
    @GetMapping("/infos")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<List<RoomInfoVO>> getRoomInfos() throws Exception {
        return ResultVO.data(serverService.getServerInfoList());
    }

}

