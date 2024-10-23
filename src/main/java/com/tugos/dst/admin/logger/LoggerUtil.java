package com.tugos.dst.admin.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtil {

    // 用户操作日志
    private static final Logger userActionLogger = LoggerFactory.getLogger("com.example.useraction");

    // 系统自动执行日志
    private static final Logger systemExecutionLogger = LoggerFactory.getLogger("com.example.systemexecution");

    // 记录用户操作日志
    public static void userLog(String message) {
        userActionLogger.info(message);
    }

    public static void logUserAction(String message, Object... args) {
        userActionLogger.info(message, args);
    }

    // 记录系统自动执行日志
    public static void systemLog(String message) {
        systemExecutionLogger.info(message);
    }

    public static void logSystemExecution(String message, Object... args) {
        systemExecutionLogger.info(message, args);
    }
}
