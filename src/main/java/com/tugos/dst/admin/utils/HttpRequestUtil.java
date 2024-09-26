package com.tugos.dst.admin.utils;

import cn.hutool.crypto.digest.DigestUtil;
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
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.tugos.dst.admin.utils.TransCoderUtil.mapToJson;


public class HttpRequestUtil {


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
                httpPost.setEntity(new UrlEncodedFormEntity(urlParameters, StandardCharsets.UTF_8));
                httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            } else if ("application/json".equals(contentType)) {
                StringEntity entity = new StringEntity(mapToJson(params), StandardCharsets.UTF_8);
                httpPost.setEntity(entity);
                httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            }

        }

        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        return entity != null ? EntityUtils.toString(entity, StandardCharsets.UTF_8) : null;
    }



}
