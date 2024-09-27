package com.tugos.dst.admin.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.config.I18nResourcesConfig;
//import com.tugos.dst.admin.service.BackupService;
import com.tugos.dst.admin.service.BackupService;
import com.tugos.dst.admin.utils.DstConstant;
import com.tugos.dst.admin.vo.BackupFileVO;
import com.tugos.dst.admin.vo.FileNamesVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author qinming
 * @date 2020-05-17
 * <p> 存档管理，游戏的存档保存在 ~/.klei/DoNotStarveTogether 目录 </p>
 */
@Controller
@RequestMapping("/backup")
@Slf4j
public class BackupController {

    private BackupService backupService;


    @GetMapping("/index")
    @RequiresAuthentication
    public String index() {
        return "/backup/index";
    }

    @GetMapping("/getBackupList")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<List<BackupFileVO>> getBackupList(@RequestParam(required = true) String roomId) {
        return ResultVO.data(backupService.getBackupFileInfo(roomId));

    }

    @GetMapping(value = "/download")
    @RequiresAuthentication
    public void download(String fileName,
                         @RequestParam(required = true) String roomId,
                         HttpServletResponse response) throws Exception {
        log.info("下载文件：" + fileName);
        backupService.download(fileName, roomId, response);
    }

    @PostMapping(value = "/upload")
    @RequiresAuthentication
    @ResponseBody
    public ResultVO<String> upload(@RequestParam("file") MultipartFile file,
                                   @RequestParam(required = true) String roomId) throws Exception {
        String suffix = FileUtil.extName(file.getOriginalFilename());
        if (!DstConstant.BACKUP_FILE_EXTENSION_NON_POINT.equalsIgnoreCase(suffix) &&
                !DstConstant.BACKUP_FILE_EXTENSION_NON_POINT_ZIP.equalsIgnoreCase(suffix)) {
            return ResultVO.fail(I18nResourcesConfig.getMessage("tip.backup.tarfile"));
        }
        return backupService.upload(file, roomId);

    }

    @PostMapping("/deleteBackup")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> deleteBackup(@RequestBody FileNamesVO fileNames,
                                         @RequestParam(required = true) String roomId) {
        log.info("删除备份:{}", JSONUtil.toJsonStr(fileNames));

        List<String> fileNamesList = fileNames.getFileNames();
        if (CollectionUtils.isNotEmpty(fileNamesList)){
            for (String s : fileNamesList) {
                backupService.deleteBackup(s, roomId);
            }
        }

        return ResultVO.success();
    }

    @GetMapping("/rename")
    @ResponseBody
    @RequiresAuthentication
    public ResultVO<String> rename(String fileName,
                                   String newFileName,
                                   @RequestParam(required = true) String roomId) {
        log.info("重命名备份:{},新文件名:{}", fileName, newFileName);
        return backupService.rename(fileName, newFileName, roomId);

    }

    @Autowired
    public void setBackupService(BackupService backupService) {
        this.backupService = backupService;
    }
}
