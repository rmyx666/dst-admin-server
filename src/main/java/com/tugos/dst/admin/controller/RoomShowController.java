package com.tugos.dst.admin.controller;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.service.RoomService;
import com.tugos.dst.admin.utils.DstConfigRoomData;
import com.tugos.dst.admin.vo.BackupFileVO;
import com.tugos.dst.admin.vo.RoomInfoVO;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import com.tugos.dst.admin.config.I18nResourcesConfig;
import com.tugos.dst.admin.entity.Menu;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

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
		return "room/index";
	}




    @PostMapping("/save")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> saveRoomInfos(@RequestBody DstConfigRoomData roomInfo) {
        return roomService.saveRoomInfos(roomInfo);
    }


	@GetMapping("/infos")
	@ResponseBody
	@RequiresAuthentication
	public ResultVO<List<RoomInfoVO>> getRoomInfos() {

		return ResultVO.data(roomService.getRoomInfos());
	}

}
