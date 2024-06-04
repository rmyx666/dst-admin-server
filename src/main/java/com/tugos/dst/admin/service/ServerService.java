package com.tugos.dst.admin.service;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.utils.DstServerInfoData;
import com.tugos.dst.admin.vo.RoomInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.tugos.dst.admin.utils.HttpRequestUtil.*;
import static com.tugos.dst.admin.utils.TransCoderUtil.convertMapToObject;

@Service
public class ServerService {

    @Autowired
    EhcacheDataService ehcacheDataService;

    @Autowired
    ServerInfoService serverInfoService;


    public List<RoomInfoVO> getServerInfoList() throws Exception {
        List<RoomInfoVO> serverRoomList = new LinkedList<>();
        List<DstServerInfoData> serverInfoList = serverInfoService.getServerInfoList();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();
        for (DstServerInfoData serverInfo : serverInfoList) {
            Future<?> future = executorService.submit(() -> {
                try {
                    Map<String, String> headers = new HashMap<>();
                    // 拼接ip获取cookie
                    String ip = "http://" + serverInfo.getIp() + ":8080";
                    String loginUrl = ip + "/login?username=" + serverInfo.getUsername() + "&password=" + serverInfo.getPassword();
                    String jsessionId = sendLoginRequest(loginUrl);

                    // 更新cookie
                    headers.put("Cookie", "JSESSIONID=" + jsessionId);
                    String result = sendGet(ip + "/room/localInfos", convertMapToObject(headers));

                    List<RoomInfoVO> roomInfoVOS = JSONUtil.toBean(JSONUtil.toJsonStr(JSONUtil.parseObj(result).get("data")), new TypeReference<List<RoomInfoVO>>() {}, false);
                    roomInfoVOS.forEach(x -> {
                        x.setServerId(serverInfo.getId());
                        x.setServerIp(serverInfo.getIp());
                        x.setServerName(serverInfo.getName());
                    });

                    synchronized (serverRoomList) {
                        serverRoomList.addAll(roomInfoVOS);
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


        return serverRoomList;
    }


}
