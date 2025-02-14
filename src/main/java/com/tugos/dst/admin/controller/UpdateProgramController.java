package com.tugos.dst.admin.controller;


import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.service.SettingService;
import com.tugos.dst.admin.service.UpdateProgramService;
import com.tugos.dst.admin.vo.GameConfigVO;
import org.springframework.beans.factory.annotation.Value;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author wgr
 * @Description 用来上传和下载更新jar包的
 * @date 2025/2/14 11:06
 */
@Controller
@RequestMapping("/update")
@Slf4j
public class UpdateProgramController {

    @Autowired
    private UpdateProgramService updateProgramService;

    @Value("${app.version}")
    private String version;

    @GetMapping("/version")
    @ResponseBody
    public String getVersion() {
        return version;
    }


    @GetMapping("/download")
    public ResponseEntity<Resource> downloadJar() {
        // 获取根目录下的dst-admin.jar文件路径
        String currentJarPath = System.getProperty("user.dir") + "/dst-admin.jar";
        Path filePath = Paths.get(currentJarPath);

        // 确保文件存在
        if (!filePath.toFile().exists()) {
            throw new RuntimeException("文件未找到！");
        }

        // 创建一个资源对象
        Resource resource = new FileSystemResource(filePath);

        // 返回文件给用户，重命名为dst-admin-update.jar
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"dst-admin-update.jar\"")
                .body(resource);
    }

    @GetMapping("/saveMasterProgramIp")
    @RequiresAuthentication
    public ResultVO<List<String>> saveMasterProgramIp(@RequestParam(required = true) String masterProgramIp) {
        updateProgramService.saveMasterProgramIp(masterProgramIp);

        return ResultVO.success();
    }

    public static void main(String[] args) {
        try {
            // 获取本机的 InetAddress
            InetAddress localHost = InetAddress.getLocalHost();
            // 获取本机的 IP 地址
            String ip = localHost.getHostAddress();
            System.out.println("本机 IP 地址: " + ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
