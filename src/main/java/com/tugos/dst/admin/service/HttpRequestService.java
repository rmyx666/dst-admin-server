package com.tugos.dst.admin.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.tugos.dst.admin.utils.GetCookieUtil.getCookie;
import static com.tugos.dst.admin.utils.HttpRequestUtil.sendGet;
import static com.tugos.dst.admin.utils.HttpRequestUtil.sendPost;


public interface HttpRequestService {


    public Object sendHttpRequest(HttpServletRequest request, HttpServletResponse httpServletResponse) throws Exception;


}
