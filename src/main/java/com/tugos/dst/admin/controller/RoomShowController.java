package com.tugos.dst.admin.controller;

import com.tugos.dst.admin.config.I18nResourcesConfig;
import com.tugos.dst.admin.entity.Menu;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
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

	@GetMapping("index")
	@RequiresPermissions("index")
	public String index() {
		return "room/index";
	}


}
