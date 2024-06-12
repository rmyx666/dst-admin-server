package com.tugos.dst.admin.service;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.tugos.dst.admin.entity.User;
import com.tugos.dst.admin.utils.DstConfigRoomData;
import com.tugos.dst.admin.utils.DstServerInfoData;
import com.tugos.dst.admin.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件存储数据
 * 全部都进行了持久化 当做数据库使用
 */
@Service
public class DataService {

    @Autowired
    private DataService self;


    @Value("${dst.username:admin}")
    private String dstUser;

    @Value("${dst.password:123456}")
    private String dstPassword;

    @Value("${dst.nickname:管理员}")
    private String nickname;



    public User getUser() {

        String path = "data/user.json";

        String userString = null;
        try {
            userString = FileUtils.readFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(userString)) {
            User user = JSONUtil.toBean(userString, User.class);
            return user;
        } else {
            User build = User.builder()
                    .username(dstUser)
                    .password(dstPassword)
                    .nickname(nickname)
                    .build();
            return build;
        }

    }



    public User updatePassword(String password) {
        User user = self.getUser();
        user.setPassword(password);

        String path = "data/user.json";

        String userString = JSONUtil.toJsonStr(user);

        try {
            FileUtils.createFile(path);
            FileUtils.writeFile(path, userString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 智能更新标志
     */

    public Boolean getSmartUpdate() {
        return true;
    }


    public Map<String, DstConfigRoomData> getRoomInfoMap() {

        String path = "data/roomInfoMap.json";

        String userString = null;
        try {
            userString = FileUtils.readFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(userString)) {
//            Map<String, DstConfigRoomData> roomInfoMap = JSONUtil.(userString, Map.class);
            Map<String, DstConfigRoomData> roomInfoMap = JSONUtil.toBean(userString, new TypeReference<Map<String, DstConfigRoomData>>() {
            }, false);
            return roomInfoMap;
        } else {
            return new HashMap<>();
        }

    }


    public Map<String, DstConfigRoomData> updateRoomInfoMap(Map<String, DstConfigRoomData> roomInfoMap) {
        String path = "data/roomInfoMap.json";
        String userString = JSONUtil.toJsonStr(roomInfoMap);
        try {
            FileUtils.createFile(path);
            FileUtils.writeFile(path, userString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomInfoMap;
    }


    public Map<Long, DstServerInfoData> getServerInfoMap() {

        String path = "data/serverInfoMap.json";

        String serverInfo = null;
        try {
            serverInfo = FileUtils.readFile(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(serverInfo)) {
            Map<Long, DstServerInfoData> serverInfoMap = JSONUtil.toBean(serverInfo, new TypeReference<Map<Long, DstServerInfoData>>() {
            }, false);
            return serverInfoMap;
        } else {
            return new HashMap<>();
        }

    }


    public Map<Long, DstServerInfoData> updateServerInfoMap(Map<Long, DstServerInfoData> serverInfoMap) {
        String path = "data/serverInfoMap.json";
        String serverInfo = JSONUtil.toJsonStr(serverInfoMap);
        try {
            FileUtils.createFile(path);
            FileUtils.writeFile(path, serverInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverInfoMap;
    }

}
