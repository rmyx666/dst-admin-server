package com.tugos.dst.admin.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

public class JsonUtils {
    public static boolean isValidJson(String str) {
        try {
            JSON.parse(str);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }
}