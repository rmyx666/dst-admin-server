package com.tugos.dst.admin.controller;

import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.vo.BackupFileVO;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 房间相关视图控制器
 */
@Controller
@RequestMapping("/room")
public class RoomShowController {

	@GetMapping("index")
	@RequiresPermissions("index")
	public String index() {
		return "roomlist/index";
	}

//	@GetMapping("/getBackupList")
//	@ResponseBody
//	@RequiresAuthentication
//	public ResultVO<List<BackupFileVO>> getBackupList(@RequestParam(required = true) String roomId) {
//		return ResultVO.data(backupService.getBackupFileInfo(roomId));
//	}

}
