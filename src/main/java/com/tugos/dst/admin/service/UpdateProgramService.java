package com.tugos.dst.admin.service;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.tugos.dst.admin.dao.SystemSettingMapper;
import com.tugos.dst.admin.entity.ServerInfo;
import com.tugos.dst.admin.entity.SystemSetting;
import com.tugos.dst.admin.utils.DstConstant;
import com.tugos.dst.admin.utils.ShellUtil;
import com.tugos.dst.admin.vo.RoomInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
                    // 目标接口 URL
                    String url = ip+"/update/download";  // 替换成实际的接口地址

                    // 创建 RestTemplate
                    RestTemplate restTemplate = new RestTemplate();

                    // 发起 GET 请求下载文件
                    ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, null, byte[].class);

                    // 获取文件内容
                    byte[] fileContent = response.getBody();

                    // 将文件保存到根目录
                    if (fileContent != null) {
                        try {
                            Path path = Paths.get(System.getProperty("user.dir"), "dst-admin-update.jar");

                            // 保存文件到根目录
                            Files.write(path, fileContent);
                            System.out.println("文件已保存到根目录：" + path.toString());

                            Thread.sleep(10000);

                            ShellUtil.runShell(DstConstant.UPDATE_JAVAPROGRAM);
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.err.println("文件保存失败！");
                        }
                    } else {
                        System.err.println("文件内容为空！");
                    }
                }
            }
        }
    }

    public void sendMasterProgramIp() throws Exception {

        // 获取本机的 InetAddress
        InetAddress localHost = InetAddress.getLocalHost();
        // 获取本机的 IP 地址
        String masterIp = localHost.getHostAddress();


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
