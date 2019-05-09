package com.fogfore.orm;

import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PropertyMapping {
    private final boolean insertable;
    private final boolean updatable;
    private final Method getter;
    private final Method setter;
    private final String columnName;
    private final String fieldName;
    private final Class enumClass;
    private final boolean isId;

    public PropertyMapping(Method getter, Method setter, Field field) {
        this.getter = getter;
        this.setter = setter;
        this.fieldName = field.getName();
        isId = field.isAnnotationPresent(Id.class);
        Column column = field.getAnnotation(Column.class);
        columnName = column == null || StringUtils.isEmpty(column.name()) ? field.getName() : column.name();
        insertable = column == null || column.insertable();
        updatable = column == null || column.updatable();
        enumClass = getter.getReturnType().isEnum() ? getter.getReturnType() : null;
    }

    @SuppressWarnings("unchecked")
    public Object get(Object object) throws Exception {
        if (object == null) {
            return null;
        }
        Object result = getter.invoke(object);
        return enumClass == null || result == null ? result : Enum.valueOf(enumClass, (String) result);
    }

    @SuppressWarnings("unchecked")
    public void set(Object object, Object value) throws Exception {
        if (enumClass != null && value != null) {
            setter.invoke(object, Enum.valueOf(enumClass, (String) value));
        }
        setter.invoke(object, setter.getParameterTypes()[0].cast(value));
    }
}