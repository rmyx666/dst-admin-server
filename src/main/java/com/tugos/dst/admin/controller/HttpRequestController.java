package com.tugos.dst.admin.controller;

import com.alibaba.fastjson.JSON;
import com.tugos.dst.admin.service.ServerInfoService;
import com.tugos.dst.admin.utils.DstServerInfoData;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static com.tugos.dst.admin.utils.HttpRequestUtil.*;
import static com.tugos.dst.admin.utils.TransCoderUtil.*;


/**
 * 通过拦截器拦截请求后，符合条件的统一走这个转发接口
 */
@Log4j2
@RestController
public class HttpRequestController {

    @Autowired
    ServerInfoService serverInfoService;

    @RequestMapping("/httpRequest")
    public Object handleAllRequests(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // 获取 URL
        String uri = (String) request.getAttribute("originalPath");
        response.put("url", uri);

        try {


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


            //获取服务器信息
            String[] servers = params.get("serverId");
            Long serverId = Long.valueOf(servers[0]);
            //移除serverId
            params.remove("serverId");
            //拼接ip获取cookie
            DstServerInfoData serverInfo = serverInfoService.getServerInfo(serverId);
            String ip = "http://" + serverInfo.getIp() + ":8080";


            String jsessionId = sendLoginRequest(ip,serverInfo.getUsername(),serverInfo.getPassword());

            //更新cookie
            headers.clear();
            headers.put("Cookie", "JSESSIONID=" + jsessionId);

            String contentType = null;
            // 获取请求体（适用于 POST 方法）
            if (method.equals(RequestMethod.POST.name())) {
                contentType = request.getContentType();
                if (contentType != null) {
                    if (contentType.contains("application/x-www-form-urlencoded")) {
                        // 解析 application/x-www-form-urlencoded
                        StringBuilder requestBody = new StringBuilder();
                        BufferedReader reader = request.getReader();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            requestBody.append(line);
                        }
                        String[] pairs = requestBody.toString().split("&");
                        Map<String, String> formParams = new HashMap<>();
                        for (String pair : pairs) {
                            String[] keyValue = pair.split("=");
                            if (keyValue.length > 1) {
                                formParams.put(keyValue[0], keyValue[1]);
                            } else {
                                formParams.put(keyValue[0], "");
                            }
                        }
                        response.put("body", formParams);
                    } else if (contentType.contains("application/json")) {
                        // 解析 application/json
                        StringBuilder requestBody = new StringBuilder();
                        BufferedReader reader = request.getReader();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            requestBody.append(line);
                        }
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> jsonParams = mapper.readValue(requestBody.toString(), Map.class);
                        response.put("body", jsonParams);
                    }
                }
            }

            String result = "";
            String paramString = "";
            Map<String, Object> paramsMap = new HashMap<>();

            if (method.equals("GET")) {
                String getQueryString = buildGetQueryString(params);
                result = sendGet(ip + uri + "?" + getQueryString, convertMapToObject(headers));


            } else if (method.equals("POST")) {

                if (contentType.contains("application/x-www-form-urlencoded")) {
                    paramsMap = convertMap(params);
                    result = sendPost(ip + uri, paramsMap, "application/x-www-form-urlencoded", convertMapToObject(headers));
                } else if (contentType.contains("application/json")) {
                    paramsMap = (Map<String, Object>) response.get("body");
                    result = sendPost(ip + uri+"?"+buildGetQueryString(params), paramsMap, "application/json", convertMapToObject(headers));
                } else {
                    //如果什么都匹配不到就按照application/x-www-form-urlencoded的调用方式走
                    paramsMap = convertMap(params);
                    result = sendPost(ip + uri, paramsMap, "application/x-www-form-urlencoded", convertMapToObject(headers));
                }

            }


//            log.info("uri:" + uri + "       params:" + paramString + "    结果：" + result);

            Object json = JSON.parse(result);
            return json;
        } catch (Exception e) {
            log.error("接口查询失败uri:" + uri, e);
        }

        return "接口查询失败uri:" + uri;
    }


}
