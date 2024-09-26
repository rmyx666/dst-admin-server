package com.tugos.dst.admin.controller;

import com.tugos.dst.admin.service.HttpRequestService;
import com.tugos.dst.admin.service.impl.HttpRequestDefaultServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 通过拦截器拦截请求后，符合条件的统一走这个转发接口
 */
@Log4j2
@RestController
public class HttpRequestController {

    @Autowired
    @Qualifier("HttpRequestDefaultServiceImpl")
    private HttpRequestService defaultService;

    @Autowired
    @Qualifier("HttpRequestFileDownloadServiceImpl")
    private HttpRequestService fileDownloadService;

    @Autowired
    @Qualifier("HttpRequestFileUploadServiceImpl")
    private HttpRequestService fileUploadService;

    public HttpRequestService getService(String url) {
        if (url.contains("/download")) {
            return fileDownloadService;
        } else if (url.contains("/upload")) {
            return fileUploadService;
        } else {
            return defaultService;
        }
    }

    @Autowired
    HttpRequestDefaultServiceImpl httpRequestDefaultServiceImpl;


    @RequestMapping("/httpRequest")
    public Object handleAllRequests(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 获取 URL
        String uri = (String) request.getAttribute("originalPath");


            HttpRequestService service = getService(uri);

            return service.sendHttpRequest(request, response);


    }


}
