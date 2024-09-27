package com.tugos.dst.admin.utils;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Title 异步定时更新java程序
 * @Description
 * @author wgr
 * @param null
 * @return
 * @date 2024/9/27 14:52
 */
@Component
public class JavaProgramUpdateUtil {
    @Async
    public void updateJavaProgram(){
        ShellUtil.runShell(DstConstant.UPDATE_JAVAPROGRAM);
    }
}
