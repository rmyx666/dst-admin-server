package com.tugos.dst.admin.service;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.tugos.dst.admin.dao.SystemSettingMapper;
import com.tugos.dst.admin.entity.ServerInfo;
import com.tugos.dst.admin.entity.SystemSetting;
import com.tugos.dst.admin.logger.LoggerUtil;
import com.tugos.dst.admin.utils.DstConstant;
import com.tugos.dst.admin.utils.ShellUtil;
import com.tugos.dst.admin.vo.RoomInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.tugos.dst.admin.utils.GetCookieUtil.getCookie;
import static com.tugos.dst.admin.utils.HttpRequestUtil.sendGet;
import static com.tugos.dst.admin.utils.JsonUtils.isValidJson;
import static com.tugos.dst.admin.utils.TransCoderUtil.convertMapToObject;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;

import org.springframework.web.client.RestTemplate;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

@Service
public class UpdateProgramService {

    @Autowired
    SystemSettingMapper systemSettingMapper;

    @Autowired
    ServerInfoService serverInfoService;

    @Value("${app.version}")
    private String oldVersion;


    public void autoUpdateProgram() throws Exception {
        SystemSetting systemSetting = systemSettingMapper.selectById(1);
        if (StringUtils.isNotBlank(systemSetting.getMasterProgramIp())) {
            String masterProgramIp = systemSetting.getMasterProgramIp();
            Map<String, String> headers = new HashMap<>();
            String ip = "http://" + masterProgramIp + ":8080";
            String newVersion = sendGet(ip + "/update/version", convertMapToObject(headers));
            if (StringUtils.isNotBlank(newVersion)) {

                int newVersionNum = Integer.parseInt(newVersion);
                int oldVersionNum = Integer.parseInt(oldVersion);

                if (newVersionNum > oldVersionNum) {
                    LoggerUtil.systemLog("开始更新Java程序 旧版本号：" + oldVersionNum + "新版本号：" + newVersionNum + "主机ip：" + masterProgramIp);
                    // 目标接口 URL
                    String url = ip + "/update/download";  // 替换成实际的接口地址


                    ShellUtil.runShell("cd ~ ; rm -f ~/log/fileDownload.log");
                    ShellUtil.runShell("cd ~ ; wget " + url + " -O dst-admin-update.jar 2>&1 | tee -a ~/log/fileDownload.log");

                    LoggerUtil.systemLog("下载完毕准备重启 旧版本号：" + oldVersionNum + "新版本号：" + newVersionNum + "主机ip：" + masterProgramIp);
                    ShellUtil.runShell(DstConstant.UPDATE_JAVAPROGRAM);


                }
            }
        }
    }

    public void sendMasterProgramIp() throws Exception {

        URL url = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
        String masterIp = in.readLine(); // 读取返回的 IP 地址


        List<ServerInfo> serverInfoList = serverInfoService.getServerInfoList();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();
        for (ServerInfo serverInfo : serverInfoList) {
            Future<?> future = executorService.submit(() -> {
                try {
                    Map<String, String> headers = new HashMap<>();
                    // 拼接ip获取cookie

                    String ip = "http://" + serverInfo.getIp() + ":8080";
                    String jsessionId = getCookie(serverInfo, false);

                    // 更新cookie
                    headers.put("Cookie", "JSESSIONID=" + jsessionId);
                    String result = sendGet(ip + "/update/saveMasterProgramIp?masterProgramIp=" + masterIp, convertMapToObject(headers));

                    boolean isJson = isValidJson(result);
                    if (!isJson) {
                        jsessionId = getCookie(serverInfo, true);

                        // 更新cookie
                        headers.put("Cookie", "JSESSIONID=" + jsessionId);
                        result = sendGet(ip + "/update/saveMasterProgramIp?masterProgramIp=" + masterIp, convertMapToObject(headers));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            futures.add(future);
        }

        // 等待所有任务完成
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
    }


    public void saveMasterProgramIp(String masterProgramIp) {
        SystemSetting systemSetting = systemSettingMapper.selectById(1L);
        systemSetting.setMasterProgramIp(masterProgramIp);
        systemSettingMapper.updateById(systemSetting);
    }
}
