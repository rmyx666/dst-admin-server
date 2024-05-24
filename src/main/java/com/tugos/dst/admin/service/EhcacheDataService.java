package com.tugos.dst.admin.service;

import com.tugos.dst.admin.entity.User;
import com.tugos.dst.admin.utils.DstConfigRoomData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ehcache缓存
 * 全部都进行了持久化 当做数据库使用
 */
@Service
public class EhcacheDataService {

    @Autowired
    private EhcacheDataService self;

    @Cacheable(value = "defaultCache", key = "'user'")
    public User getUser() {
        User build = User.builder()
                .username("admin")
                .password("123456")
                .nickname("admin")
                .build();
        return build;
    }


    @CachePut(value = "defaultCache", key = "'user'")
    public User updatePassword(String password) {
        User user = self.getUser();
        user.setPassword(password);
        return user;
    }

    /**
     * 智能更新标志
     */
    @Cacheable(value = "defaultCache", key = "'smartUpdate'")
    public Boolean getSmartUpdate() {
        return true;
    }

    @Cacheable(value = "defaultCache", key = "'roomInfoMap'")
    public Map<String, DstConfigRoomData> getRoomInfoMap() {
        return new HashMap<>();
    }

    @CachePut(value = "defaultCache", key = "'roomInfoMap'")
    public Map<String, DstConfigRoomData> updateRoomInfoMap(Map<String, DstConfigRoomData> roomInfoMap) {
        return roomInfoMap;
    }



}
