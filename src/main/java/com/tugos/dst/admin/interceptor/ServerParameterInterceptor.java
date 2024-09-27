package com.tugos.dst.admin.interceptor;

import com.tugos.dst.admin.controller.HttpRequestController;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截param中存在serverId参数的请求，转发到转发用的接口
 */
@Component
public class ServerParameterInterceptor implements HandlerInterceptor {

    @Autowired
    HttpRequestController httpRequestController;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查请求参数是否包含 key 为 "server" 且value不为空

        if (request.getParameterMap().containsKey("serverId")) {
            String[] servers = request.getParameterMap().get("serverId");
            if (StringUtils.isNotBlank(servers[0]) && !servers[0].equals("null")) {
                String originalPath = request.getRequestURI();
                request.setAttribute("originalPath", originalPath);
                request.getRequestDispatcher("/httpRequest").forward(request, response);

                return false; // 阻止原始请求继续处理
            }
            // 将请求转发到 /server 处理

        }
        return true; // 继续处理原始请求
    }
}
