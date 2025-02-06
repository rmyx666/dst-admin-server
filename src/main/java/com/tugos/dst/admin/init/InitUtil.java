package com.tugos.dst.admin.init;

import com.tugos.dst.admin.dao.RoomInfoMapper;
import com.tugos.dst.admin.dao.DstServerInfoDataMapper;
import com.tugos.dst.admin.dao.UserMapper;
import com.tugos.dst.admin.entity.User;
import com.tugos.dst.admin.service.DataService;
import com.tugos.dst.admin.systementity.Server;
import com.tugos.dst.admin.systementity.serverInfo.Cpu;
import com.tugos.dst.admin.utils.DstConstant;
import com.tugos.dst.admin.utils.FileUtils;
import com.tugos.dst.admin.utils.ShellUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * @author wgr
 * @Title 项目启动的时候初始化的工具
 * @Description
 * @date 2024/10/17 15:25
 */
@Component
@Log4j2
public class InitUtil {

    @Autowired
    DataService dataService;
    @Autowired
    RoomInfoMapper roomInfoMapper;
    @Autowired
    DstServerInfoDataMapper dstServerInfoDataMapper;
    @Autowired
    UserMapper userMapper;

    @Value("${dst.username:admin}")
    private String dstUser;

    @Value("${dst.password:123456}")
    private String dstPassword;

    @Value("${dst.nickname:管理员}")
    private String nickname;

    public static Integer CPU_NUM = 0;


    /**
     * 启动时释放安装dst游戏脚本
     * 读取文件中存储的数据到缓存中
     */
    @PostConstruct
    public void initSystem() throws Exception {

        //释放脚本并授权
        copyAndChmod(DstConstant.INSTALL_DST);
        copyAndChmod(DstConstant.DST_START);
        copyAndChmod(DstConstant.UPDATE);
        copyAndChmod(DstConstant.RESTART);
        ShellUtil.runShell("sed -i 's/\\r//' ~/dstStart.sh");
        ShellUtil.runShell("sed -i 's/\\r//' ~/install.sh");
        ShellUtil.runShell("sed -i 's/\\r//' ~/update.sh");
        ShellUtil.runShell("sed -i 's/\\r//' ~/restart.sh");


    }

    @PostConstruct
    public void initUser() throws Exception {
        if (userMapper.selectById(dstUser) == null) {
            User user = User.builder()
                    .username(dstUser)
                    .password(dstPassword)
                    .nickname(nickname)
                    .build();
            userMapper.insert(user);
        }


    }

/**
 * @Title initCpuNum
 * @Description 获取当前cpu核数 目前不需要
 * @author wgr
 * @return void
 * @date 2025/2/6 14:39
 */
//    @PostConstruct
//    public void initCpuNum() throws Exception {
//        //获取硬件信息
//        Server server = new Server();
//        server.copyTo();
//        Cpu cpu = server.getCpu();
//        CPU_NUM = cpu.getCpuNum();
//    }

    /**
     * 释放脚本并授权
     */
    private void copyAndChmod(String fileName) throws Exception {
        boolean copy = FileUtils.fileShellCopy(fileName);
        if (copy) {
            FileUtils.chmod(fileName);
        }
    }
}
