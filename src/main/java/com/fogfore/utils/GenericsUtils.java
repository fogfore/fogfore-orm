package com.fogfore.utils;

import org.apache.commons.lang3.ObjectUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericsUtils {
    public static Class getSuperClassGenericType(Class clazz) {
        return getSuperClassGenericType(clazz, 0);
    }

    private static Class getSuperClassGenericType(Class clazz, int i) {
        if (ObjectUtils.isEmpty(clazz)) {
            return null;
        }
        Type genType = clazz.getGenericSuperclass();
        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        Type[] types = ((ParameterizedType) genType).getActualTypeArguments();
        if (i < 0 || i >= types.length) {
            throw new IndexOutOfBoundsException();
        }
        if (!(types[i] instanceof Class)) {
            return Object.class;
        }
        return (Class) types[0];
    }
}
