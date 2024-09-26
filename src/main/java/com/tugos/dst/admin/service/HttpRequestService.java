package com.tugos.dst.admin.service;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
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


public interface HttpRequestService {


    public Object sendHttpRequest(HttpServletRequest request, HttpServletResponse httpServletResponse) throws Exception;


}
