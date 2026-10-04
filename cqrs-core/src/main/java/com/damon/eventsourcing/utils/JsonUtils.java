package com.damon.eventsourcing.utils;

import com.alibaba.fastjson.JSONObject;

public class JsonUtils {
    public static <T> T toObj(String json, Class<?> cls) {
        return (T) JSONObject.parseObject(json, cls);
    }

    public static String toStr(Object obj) {
        return JSONObject.toJSONString(obj);
    }


}
