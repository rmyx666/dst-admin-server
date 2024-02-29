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

	@GetMapping("/room_main")
	@RequiresPermissions("index")
	public String room_main(Model model) {
		Menu menu = Menu.builder().id(2L).icon("layui-icon layui-icon-home").sort(0).
				children(new HashMap<>()).title(I18nResourcesConfig.getMessage("main.menu1.name")).type(1).url("/home/index").build();

		Menu menu1 = Menu.builder().id(2L).icon("layui-icon layui-icon-set").sort(1).
				children(new HashMap<>()).title(I18nResourcesConfig.getMessage("main.menu2.name")).type(1).url("/setting/index").build();

		Menu menu2 = Menu.builder().id(4L).icon("layui-icon layui-icon-group").sort(2).
				children(new HashMap<>()).title(I18nResourcesConfig.getMessage("main.menu3.name")).type(1).url("/player/index").build();

		Menu menu3 = Menu.builder().id(2L).icon("layui-icon layui-icon-log").sort(3).
				children(new HashMap<>()).title(I18nResourcesConfig.getMessage("main.menu4.name")).type(1).url("/backup/index").build();

		Menu menu4 = Menu.builder().id(3L).icon("layui-icon layui-icon-survey").sort(4).
				children(new HashMap<>()).title(I18nResourcesConfig.getMessage("main.menu5.name")).type(1).url("/system/guide").build();

		Menu menu5 = Menu.builder().id(4L).icon("layui-icon layui-icon-util").sort(5).
				children(new HashMap<>()).title(I18nResourcesConfig.getMessage("main.menu6.name")).type(1).url("/system/index").build();


		Map<String, Menu> treeMenu = new HashMap<>(16);
		treeMenu.put("0", menu);
		treeMenu.put("1", menu1);
		treeMenu.put("2", menu2);
		treeMenu.put("3", menu3);
		treeMenu.put("4", menu4);
		treeMenu.put("5", menu5);

		model.addAttribute("treeMenu", treeMenu);
		return "room/room_main";
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
