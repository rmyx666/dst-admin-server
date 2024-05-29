package com.tugos.dst.admin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static com.tugos.dst.admin.utils.TransCoderUtil.mapToJson;


public class HttpRequestUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String sendGet(String url, Map<String, Object> headers) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        // Add headers
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), (String) entry.getValue());
            }
        }

        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        return entity != null ? EntityUtils.toString(entity) : null;
    }

    public static String sendPost(String url, Map<String, Object> params, String contentType, Map<String, Object> headers) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        // Add headers
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), (String) entry.getValue());
            }
        }

        if (!params.isEmpty()) {
            if ("application/x-www-form-urlencoded".equals(contentType)) {
                List<BasicNameValuePair> urlParameters = new ArrayList<>();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    urlParameters.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
            } else if ("application/json".equals(contentType)) {
                StringEntity entity = new StringEntity(mapToJson(params));
                httpPost.setEntity(entity);
                httpPost.setHeader("Content-Type", "application/json");
            }

        }

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        return entity != null ? EntityUtils.toString(entity) : null;
    }

    public static String sendLoginRequest(String loginUrl) throws Exception {
        java.net.URL url = new URL(loginUrl);
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


}
