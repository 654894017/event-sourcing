package com.damon.eventsourcing.utils;

import cn.hutool.core.util.ReflectUtil;

public class ReflectUtils {

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> classes) {
        return (T) ReflectUtil.newInstance(classes);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(String classTypeName) {
        try {
            return (Class<T>) Class.forName(classTypeName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
