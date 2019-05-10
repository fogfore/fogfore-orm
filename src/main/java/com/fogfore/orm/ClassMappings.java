package com.fogfore.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

public class ClassMappings {
    private static final Set<Class<?>> SUPPORTED_SQL_OBJECTS = new HashSet<>();

    static {
        Class<?>[] classes = {
                boolean.class, Boolean.class,
                char.class, Character.class,
                byte.class, Byte.class,
                short.class, Short.class,
                int.class, Integer.class,
                long.class, Long.class,
                float.class, Float.class,
                double.class, Double.class,
                String.class,
                BigDecimal.class,
                Date.class,
                Timestamp.class
        };
        SUPPORTED_SQL_OBJECTS.addAll(Arrays.asList(classes));
    }

    private static boolean isSupportedSqlObject(Class<?> clazz) {
        return clazz != null && (SUPPORTED_SQL_OBJECTS.contains(clazz) || clazz.isEnum());
    }

    public static Map<String, Method> findPublicGetters(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Map<String, Method> map = new HashMap<>();
        Method[] methods = clazz.getMethods();
        String propertyName;
        for (Method method : methods) {
            if (Modifier.isAbstract(method.getModifiers()) ||
                    Modifier.isStatic(method.getModifiers()) ||
                    method.getParameterCount() != 0 ||
                    !isSupportedSqlObject(method.getReturnType()) ||
                    "getClass".equals(method.getName())) {
                continue;
            }
            if (boolean.class.equals(method.getReturnType()) ||
                    Boolean.class.equals(method.getReturnType()) ||
                    (method.getName().startsWith("is") &&
                            method.getName().length() >= 3)) {
                map.put(getGetterName(method), method);
            }
            if (method.getName().startsWith("get") &&
                    method.getName().length() >= 4) {
                map.put(getGetterName(method), method);
            }
        }
        return map;
    }

    public static Map<String, Method> findPublicSetter(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Map<String, Method> map = new HashMap<>();
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (Modifier.isAbstract(method.getModifiers()) ||
                    Modifier.isStatic(method.getModifiers()) ||
                    !void.class.equals(method.getReturnType()) ||
                    !method.getName().startsWith("set") ||
                    method.getName().length() < 4 ||
                    method.getParameterCount() != 1 ||
                    !isSupportedSqlObject(method.getParameterTypes()[0])) {
                continue;
            }
            map.put(getSetterName(method), method);
        }
        return map;
    }

    public static Field[] getFields(Class<?> clazz) {
        return clazz != null ? clazz.getDeclaredFields() : null;
    }

    public static String getGetterName(Method method) {
        if (method == null) {
            return null;
        }
        String name = null;
        if (method.getName().startsWith("is")) {
            name = method.getName().substring(2);
        }
        if (method.getName().startsWith("get")) {
            name = method.getName().substring(3);
        }
        return firstLowerCase(name);
    }

    public static String getSetterName(Method method) {
        if (method == null || !method.getName().startsWith("set")) {
            return null;
        }
        String name = method.getName().substring(3);
        return firstLowerCase(name);
    }

    private static String firstLowerCase(String str) {
        if (str == null) {
            return null;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}