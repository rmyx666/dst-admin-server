package com.tugos.dst.admin.interceptor;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ServerParameterInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查请求参数是否包含 key 为 "server" 且value不为空

        if (request.getParameterMap().containsKey("server") ) {
            String[] servers = request.getParameterMap().get("server");
            if (StringUtils.isNotBlank(servers[0])) {
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
