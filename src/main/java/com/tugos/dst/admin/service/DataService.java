package com.tugos.dst.admin.service;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.tugos.dst.admin.dao.UserMapper;
import com.tugos.dst.admin.entity.DstServerInfoData;
import com.tugos.dst.admin.entity.User;
import com.tugos.dst.admin.entity.DstConfigRoomData;
import com.tugos.dst.admin.utils.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
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
    @Autowired
    UserMapper userMapper;

    @Value("${dst.username:admin}")
    private String dstUser;

    @Value("${dst.password:123456}")
    private String dstPassword;

    @Value("${dst.nickname:管理员}")
    private String nickname;


    public User getUser() {

        User admin = userMapper.selectById(dstUser);
        return admin;


    }


    public void updatePassword(String password) {
        User admin = userMapper.selectById(dstUser);
        admin.setPassword(password);
        userMapper.updateById(admin);

    }

    /**
     * 智能更新标志
     */

    public Boolean getSmartUpdate() {
        return true;
    }


}
