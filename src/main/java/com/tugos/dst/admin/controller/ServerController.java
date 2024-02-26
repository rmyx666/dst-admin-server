package com.tugos.dst.admin.controller;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tugos.dst.admin.common.ResultCodeEnum;
import com.tugos.dst.admin.common.ResultVO;
import com.tugos.dst.admin.entity.User;
import com.tugos.dst.admin.utils.DstConfigData;
import com.tugos.dst.admin.vo.GameArchiveVO;
import com.tugos.dst.admin.vo.ServerInfoVO;
import com.tugos.dst.admin.vo.UpdatePwdVO;
import com.tugos.dst.admin.vo.UpdateUserDetailVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qinming
 * @date 2020-05-16
 * <p> 用户管理控制器 </p>
 */
@Controller
@RequestMapping("/server")
public class ServerController {

    @GetMapping("/index")
    @RequiresAuthentication
    public String index() {
        return "/server/index";
    }


    /**
     * 获取用户头像
     */
    @GetMapping("/serverInfo")
    @ResponseBody
    public ResultVO serverInfo()  {

        List<String> ips = new ArrayList<>();
        ips.add("http://43.143.77.180:8080");
        ips.add("http://114.115.165.164:8080");
        ips.add("http://156.236.75.110:8080");
        ips.add("http://49.232.171.42:8080");
        ips.add("http://120.26.201.156:8080");

        List<ServerInfoVO> serverInfoVOS= new ArrayList<>();
        for (String ip : ips) {
            ServerInfoVO serverInfo = getServerInfo(ip);
            serverInfoVOS.add(serverInfo);
        }

        return ResultVO.data(serverInfoVOS);

    }


    private static ServerInfoVO getServerInfo(String ip)  {
        try {
            ServerInfoVO serverInfoVO= new ServerInfoVO();


            String loginUrl = ip + "/login?username=admin&password=wangguanru1234";
            String jsessionId = sendLoginRequest(loginUrl);

            // 打印获取的 JSESSIONID
            System.out.println("JSESSIONID: " + jsessionId);


            // 使用获取的 JSESSIONID 发送后续请求
            // 获取用户列表
            String getServerInfoUrl = ip + "/home/getGameArchive";
            String ServerInfoResponse = sendRequestWithJSessionId(getServerInfoUrl, jsessionId);

            ResultVO ServerInfoResult = JSONUtil.toBean(ServerInfoResponse, ResultVO.class);
            GameArchiveVO gameArchiveVO = JSONUtil.toBean(JSONUtil.toJsonStr(ServerInfoResult.getData()), GameArchiveVO.class);



            // 使用获取的 JSESSIONID 发送后续请求
            // 获取用户列表
            String getPlayerListUrl = ip + "/home/getPlayerList";
            String playerListResponse = sendRequestWithJSessionId(getPlayerListUrl, jsessionId);
            System.out.println(playerListResponse);

            ResultVO playerListResult = JSONUtil.toBean(playerListResponse, ResultVO.class);
            List<String> playerList = JSONUtil.toList(JSONUtil.toJsonStr(playerListResult.getData()), String.class);

            BeanUtils.copyProperties(gameArchiveVO,serverInfoVO);
            serverInfoVO.setNowPlayers(playerList.size());
            serverInfoVO.setPlayerList(playerList);

            return serverInfoVO;




        } catch (Exception e) {            // 发送登录请求


            e.printStackTrace();
        }
        return new ServerInfoVO();
    }

    private static String sendLoginRequest(String loginUrl) throws Exception {
        URL url = new URL(loginUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // 设置请求方法为GET
        connection.setRequestMethod("POST");

        // 获取所有的Set-Cookie头字段
        List<String> cookies = getAllCookies(connection);

        // 输出完整的响应头
        System.out.println("Response Headers: " + connection.getHeaderFields());

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

    private static String sendRequestWithJSessionId(String url, String jsessionId) throws Exception {
        URL getUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();

        // 设置请求方法为GET
        connection.setRequestMethod("GET");

        // 设置 JSESSIONID Cookie
        connection.setRequestProperty("Cookie", "JSESSIONID=" + jsessionId);

        // 读取响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        // 关闭连接
        connection.disconnect();

        return response.toString();
    }
}

