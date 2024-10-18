package com.tugos.dst.admin.utils;

import cn.hutool.crypto.digest.DigestUtil;
import com.tugos.dst.admin.entity.DstServerInfoData;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author wgr
 * @Title 获取cookie
 * @Description
 * @return
 * @date 2024/9/25 11:55
 */
public class GetCookieUtil {


    private static HashMap<String, String> cookieMap = new HashMap();

    /**
     * @param serverInfo
     * @param refresh    为true重新刷新 为false 丛map中取
     * @return java.lang.String
     * @author wgr
     * @Title 获取cookie 如果map中有就直接从map中取 没有就重新获取
     * @Description
     * @date 2024/9/25 14:55
     */
    public static String getCookie(DstServerInfoData serverInfo, Boolean refresh) throws Exception {

        if (refresh) {
            String ip = "http://" + serverInfo.getIp() + ":8080";
            String cookie = getCookie(ip, serverInfo.getUsername(), serverInfo.getPassword());
            cookieMap.put(serverInfo.getIp(), cookie);
            return cookie;
        } else {

            if (cookieMap.containsKey(serverInfo.getIp())) {
                return cookieMap.get(serverInfo.getIp());
            } else {
                String ip = "http://" + serverInfo.getIp() + ":8080";
                String cookie = getCookie(ip, serverInfo.getUsername(), serverInfo.getPassword());
                cookieMap.put(serverInfo.getIp(), cookie);
                return cookie;
            }
        }


    }

    public static String getCookie(String ip, String username, String password) throws Exception {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String loginUrl = ip + "/login?username=" + DigestUtil.md5Hex("user" + username + timestamp) +
                "&password=" + DigestUtil.md5Hex("password" + password + timestamp) +
                "&timestamp=" + timestamp;
        URL url = new URL(loginUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 设置请求方法为GET
        connection.setRequestMethod("POST");

        // 获取所有的Set-Cookie头字段
        List<String> cookies = getAllCookies(connection);

        // 输出完整的响应头
//        System.out.println("Response Headers: " + connection.getHeaderFields());

        // 解析 JSESSIONID 的值
        String jsessionId = extractJSessionId(cookies);

        // 关闭连接
        connection.disconnect();

        return jsessionId;
    }

    private static List<String> getAllCookies(HttpURLConnection connection) {
        List<String> cookies = new ArrayList<>();
        int headerIndex = 1; // Set-Cookie头字段的索引从1开始

        while (true) {
            // 逐个获取Set-Cookie头字段
            String cookieHeader = connection.getHeaderField(headerIndex);
            if (cookieHeader == null) {
                break; // 没有更多的Set-Cookie头字段了
            }

            cookies.add(cookieHeader);
            headerIndex++;
        }

        return cookies;
    }

    private static String extractJSessionId(List<String> cookies) {
        for (String cookie : cookies) {
            // 在每个Set-Cookie头字段中寻找JSESSIONID
            if (cookie.contains("JSESSIONID")) {
                return parseJSessionId(cookie);
            }
        }

        return null; // 没有找到JSESSIONID
    }

    private static String parseJSessionId(String cookieHeader) {
        if (cookieHeader != null) {
            String[] cookies = cookieHeader.split(";");
            for (String cookie : cookies) {
                if (cookie.trim().startsWith("JSESSIONID=")) {
                    return cookie.trim().substring("JSESSIONID=".length());
                }
            }
        }
        return null;
    }


}
