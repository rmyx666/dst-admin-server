package com.tugos.dst.admin.controller;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.service.RoomService;
import com.tugos.dst.admin.entity.DstConfigRoomData;
import com.tugos.dst.admin.vo.RoomInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间相关视图控制器
 */
@Api(tags = "Room Management")
@Controller
@RequestMapping("/room")
public class RoomController {

    @Autowired
    RoomService roomService;

    @GetMapping("index")
    @RequiresPermissions("index")
    public String index() {
        return "/room/index";
    }

    @ApiOperation(value = "获取房间信息", notes = "")
    @GetMapping("/localInfos")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<List<RoomInfoVO>> getLocalRoomInfos() throws Exception {
        return ResultVO.data(roomService.getLocalRoomInfos());
    }

    @ApiOperation(value = "获取房间信息", notes = "包含cpu 内存硬件信息")
    @GetMapping("/localInfosWithHardware")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<List<RoomInfoVO>> getLocalRoomInfosWithHardware() throws Exception {
        return ResultVO.data(roomService.getLocalRoomInfosWithHardware());
    }


    @ApiOperation(value = "远程服务器信息", notes = "调用获取远程服务器的房间信息")
    @GetMapping("/serverInfos")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<List<RoomInfoVO>> getRoomInfos() throws Exception {
        return ResultVO.data(roomService.getServerRoomInfos());
    }

    @ApiOperation(value = "保存房间信息", notes = "保存房间的信息")
    @PostMapping("/save")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> saveRoomInfos(@RequestBody DstConfigRoomData roomInfo) {
        return roomService.saveRoomInfos(roomInfo);
    }

    @ApiOperation(value = "删除房间信息", notes = "根据房间ID删除房间信息")
    @GetMapping("/del")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> delRoomInfos(@RequestParam String roomId) throws Exception {
        return roomService.delRoomInfos(roomId);
    }

    @ApiOperation(value = "更新房间信息", notes = "更新房间的信息 更新不能更改roomid，其他四个可以更改")
    @PostMapping("/update")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> updateRoomInfos(@RequestBody DstConfigRoomData roomInfo) throws Exception {
        return roomService.updateRoomInfos(roomInfo);
    }

}
