package com.tugos.dst.admin.controller;

import com.alibaba.fastjson.JSON;
import com.tugos.dst.admin.service.HttpRequestService;
import com.tugos.dst.admin.service.ServerInfoService;
import com.tugos.dst.admin.utils.DstServerInfoData;
import lombok.extern.log4j.Log4j2;
import org.checkerframework.checker.units.qual.A;
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

    @Autowired
    HttpRequestService httpRequestService;


    @RequestMapping("/httpRequest")
    public Object handleAllRequests(HttpServletRequest request) {


        // 获取 URL
        String uri = (String) request.getAttribute("originalPath");


        try {

            return httpRequestService.sendHttpRequest(request);

        } catch (Exception e) {
            log.error("接口查询失败uri:" + uri, e);
        }

        return "接口查询失败uri:" + uri;
    }


}
