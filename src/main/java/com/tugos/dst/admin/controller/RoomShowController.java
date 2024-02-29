package com.tugos.dst.admin.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 房间相关视图控制器
 */
@Controller
@RequestMapping("/room")
public class RoomShowController {

	@GetMapping("infos")
	@RequiresPermissions("index")
	public String index() {
		return "/room/infos";
	}


}
