package com.tugos.dst.admin.utils;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.tugos.dst.admin.utils.TransCoderUtil.mapToJson;


public class HttpRequestUtil {

    /**
     * @param url
     * @param headers
     * @return java.lang.String
     * @Title 默认的get请求方法
     * @Description
     * @author wgr
     * @date 2024/9/26 14:13
     */
    public static String sendGet(String url, Map<String, Object> headers) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        // Add headers
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpGet.addHeader(entry.getKey(), (String) entry.getValue());
            }
        }
        httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        return entity != null ? EntityUtils.toString(entity) : null;
    }

    /**
     * @param url
     * @param params
     * @param contentType
     * @param headers
     * @return java.lang.String
     * @Title 默认的post请求方法
     * @Description
     * @author wgr
     * @date 2024/9/26 14:13
     */
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
            if (contentType.contains("application/x-www-form-urlencoded")) {
                List<BasicNameValuePair> urlParameters = new ArrayList<>();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    urlParameters.add(new BasicNameValuePair(entry.getKey(), (String) entry.getValue()));
                }
                httpPost.setEntity(new UrlEncodedFormEntity(urlParameters, StandardCharsets.UTF_8));
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            } else if (contentType.contains("application/json")) {
                StringEntity entity = new StringEntity(mapToJson(params), StandardCharsets.UTF_8);
                httpPost.setEntity(entity);
                httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            }

        }

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        return entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : null;
    }

    /**
     * @param url
     * @param params
     * @param contentType
     * @param headers
     * @param file
     * @return java.lang.String
     * @Title 专门上传文件的post请求
     * @Description
     * @author wgr
     * @date 2024/9/26 14:52
     */
    public static String sendPost(String url, Map<String, Object> params, String contentType, Map<String, Object> headers, MultipartFile file) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        // Add headers
        if (headers != null) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                httpPost.addHeader(entry.getKey(), (String) entry.getValue());
            }
        }

        // Handle file upload with multipart/form-data
        if (file != null && !file.isEmpty() && contentType.contains("multipart/form-data")) {

            MultipartEntityBuilder builder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.RFC6532);;



            // Add file to the request with correct filename encoding
            builder.addBinaryBody("file", file.getInputStream(),  ContentType.MULTIPART_FORM_DATA, file.getOriginalFilename());

            // Add additional parameters (non-file form fields)
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    builder.addTextBody(entry.getKey(), entry.getValue().toString(), ContentType.TEXT_PLAIN);
                }
            }

            // Set the entity to the HttpPost
            HttpEntity multipart = builder.build();
            httpPost.setEntity(multipart);
        }

        // Execute the request and get the response
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        return entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : null;
    }
}
