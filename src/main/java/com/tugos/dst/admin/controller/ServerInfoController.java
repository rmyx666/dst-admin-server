package com.tugos.dst.admin.controller;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.service.ServerInfoService;
import com.tugos.dst.admin.utils.DstServerInfoData;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/serverInfo")
public class ServerInfoController {

    @GetMapping("index")
    @RequiresPermissions("index")
    public String index() {
        return "/serverInfo/index";
    }

    @Autowired
    private ServerInfoService serverInfoService;

    @ApiOperation(value = "获取服务器信息", notes = "获取所有服务器的信息")
    @GetMapping("/infos")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<List<DstServerInfoData> > getServerInfos() throws Exception {
        return ResultVO.data(serverInfoService.getServerInfoList());
    }

    @ApiOperation(value = "保存服务器信息", notes = "保存服务器的信息")
    @PostMapping("/save")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> saveServerInfo(@RequestBody DstServerInfoData serverInfo) {

        return         serverInfoService.saveServerInfo(serverInfo);
    }

    @ApiOperation(value = "删除服务器信息", notes = "根据IP删除服务器信息")
    @GetMapping("/del")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> deleteServerInfo(@RequestParam Long id) throws Exception {
        serverInfoService.deleteServerInfo(id);
        return ResultVO.success("服务器信息删除成功");
    }

    @ApiOperation(value = "更新服务器信息", notes = "更新服务器的信息")
    @PostMapping("/update")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> updateServerInfo(@RequestBody DstServerInfoData serverInfo) throws Exception {
        serverInfoService.updateServerInfo(serverInfo);
        return ResultVO.success("服务器信息更新成功");
    }
}