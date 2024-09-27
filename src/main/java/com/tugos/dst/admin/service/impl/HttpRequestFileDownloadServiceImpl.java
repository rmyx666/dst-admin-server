package com.tugos.dst.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tugos.dst.admin.service.HttpRequestService;
import com.tugos.dst.admin.service.ServerInfoService;
import com.tugos.dst.admin.utils.DstServerInfoData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.tugos.dst.admin.utils.GetCookieUtil.getCookie;
import static com.tugos.dst.admin.utils.HttpRequestUtil.sendGet;
import static com.tugos.dst.admin.utils.HttpRequestUtil.sendPost;
import static com.tugos.dst.admin.utils.JsonUtils.isValidJson;
import static com.tugos.dst.admin.utils.TransCoderUtil.*;

@Service("HttpRequestFileDownloadServiceImpl")
@Slf4j
public class HttpRequestFileDownloadServiceImpl implements HttpRequestService {
    @Autowired
    ServerInfoService serverInfoService;


    @Override
    public Object sendHttpRequest(HttpServletRequest request, HttpServletResponse httpServletResponse) throws Exception {

        Map<String, Object> response = new HashMap<>();

        // 获取 URL
        String uri = (String) request.getAttribute("originalPath");
        response.put("url", uri);


        // 获取请求方法
        String method = request.getMethod();
        response.put("method", method);

        // 获取请求头信息
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        response.put("headers", headers);

        // 获取请求参数
        Map<String, String[]> modifiableParams = request.getParameterMap();
        // 创建一个新的可修改的 HashMap
        Map<String, String[]> params = new HashMap<>();

        // 将原始映射的内容复制到新的可修改的 HashMap 中
        for (Map.Entry<String, String[]> entry : modifiableParams.entrySet()) {
            params.put(entry.getKey(), entry.getValue().clone());
        }

        response.put("params", params);


        String result = "";
        Map<String, Object> paramsMap = new HashMap<>();


        //获取服务器信息
        String[] servers = params.get("serverId");
        Long serverId = Long.valueOf(servers[0]);
        //移除serverId
        params.remove("serverId");
        //拼接ip获取cookie
        DstServerInfoData serverInfo = serverInfoService.getServerInfo(serverId);
        String ip = "http://" + serverInfo.getIp() + ":8080";


        String jsessionId = getCookie(serverInfo, false);

        //更新cookie
        headers.clear();
        headers.put("Cookie", "JSESSIONID=" + jsessionId);


        if (method.equals("GET")) {
            String getQueryString = buildGetQueryString(params);
            result = sendGet(ip + uri + "?" + getQueryString, convertMapToObject(headers), httpServletResponse);
        }

        boolean isJson = isValidJson(result);

        if (!isJson) {

            jsessionId = getCookie(serverInfo, true);
            //更新cookie
            headers.clear();
            headers.put("Cookie", "JSESSIONID=" + jsessionId);

            if (method.equals("GET")) {
                String getQueryString = buildGetQueryString(params);
                result = sendGet(ip + uri + "?" + getQueryString, convertMapToObject(headers), httpServletResponse);
            }


        }


        return null;

    }


}
