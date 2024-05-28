package com.tugos.dst.admin.utils;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class TransCoderUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Map<String, Object> jsonToMap(String json) throws IOException {
        if (StringUtils.isNotBlank(json)) {
            return objectMapper.readValue(json, Map.class);
        } else {
            return null;
        }

    }

    public static String mapToJson(Map<String, Object> map) throws JsonProcessingException {
        return objectMapper.writeValueAsString(map);
    }

    public static Map<String, Object> urlencodedToMap(String query) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(query))return new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
            String value = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
            map.put(key, value);
        }
        return map;
    }

    public static String mapToStringSortedByKey(Map<String, Object> map) {

        if (map.isEmpty())return null;

        // 使用 TreeMap 对键进行排序
        TreeMap<String, Object> sortedMap = new TreeMap<>(map);

        // 构建结果字符串
        StringBuilder result = new StringBuilder();

        // 遍历排序后的键值对，并构建字符串
        for (Map.Entry<String, Object> entry : sortedMap.entrySet()) {
            result.append(entry.getKey()).append("_").append(entry.getValue()).append(":");
        }

        // 删除最后一个冒号
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    public static Map<String, Object> convertMap(Map<String, String[]> params) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            if (values != null && values.length > 0) {
                result.put(key, values[0]);
            }
        }
        return result;
    }
}
