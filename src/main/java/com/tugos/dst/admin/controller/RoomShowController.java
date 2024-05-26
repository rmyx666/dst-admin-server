package com.tugos.dst.admin.controller;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.service.RoomService;
import com.tugos.dst.admin.utils.DstConfigRoomData;
import com.tugos.dst.admin.vo.RoomInfoVO;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房间相关视图控制器
 */
@Controller
@RequestMapping("/room")
public class RoomShowController {

    @Autowired
    RoomService roomService;

    @GetMapping("index")
    @RequiresPermissions("index")
    public String index() {
        return "/room/index";
    }

    @GetMapping("/infos")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<List<RoomInfoVO>> getRoomInfos() throws Exception {
        return ResultVO.data(roomService.getRoomInfos());
    }

    @PostMapping("/save")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> saveRoomInfos(@RequestBody DstConfigRoomData roomInfo) {
        return roomService.saveRoomInfos(roomInfo);
    }

	@GetMapping("/del")
	@ResponseBody
	@RequiresAuthentication
	public ResultVO<String> delRoomInfos(@RequestParam String roomId) throws Exception {
		return roomService.delRoomInfos(roomId);
	}

	@GetMapping("/update")
	@ResponseBody
	@RequiresAuthentication
	public ResultVO<String> updateRoomInfos(@RequestBody DstConfigRoomData roomInfo) throws Exception {
		return roomService.updateRoomInfos(roomInfo);
	}

}
