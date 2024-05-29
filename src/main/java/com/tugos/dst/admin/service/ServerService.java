package com.tugos.dst.admin.service;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.utils.DstServerInfoData;
import com.tugos.dst.admin.vo.RoomInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
        for (DstServerInfoData serverInfo : serverInfoList) {
            Map<String, String> headers = new HashMap<>();
            //拼接ip获取cookie

            String ip = "http://" + serverInfo.getIp() + ":8080";

            String loginUrl = ip + "/login?username=" + serverInfo.getUsername() + "&password=" + serverInfo.getPassword();
            String jsessionId = sendLoginRequest(loginUrl);

            //更新cookie
            headers.remove("Cookie");
            headers.put("Cookie", "JSESSIONID=" + jsessionId);
            String result = sendGet(ip + "/room/infos", convertMapToObject(headers));
            List<RoomInfoVO> roomInfoVOS = JSONUtil.toBean(JSONUtil.toJsonStr(JSONUtil.parseObj(result).get("data")), new TypeReference<List<RoomInfoVO>>() {}, false);
            roomInfoVOS.forEach(x->x.setServerId(serverInfo.getId()));
            serverRoomList.addAll(roomInfoVOS);
        }
        return serverRoomList;
    }


}
