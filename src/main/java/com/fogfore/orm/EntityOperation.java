package com.fogfore.orm;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class EntityOperation<T> {
    private final Class<T> entityClass;
    private final String tableName;
    private final Map<String, PropertyMapping> mappings;
    private final RowMapper<T> rowMapper;
    private String allColumn = "*";

    public EntityOperation(Class<T> clazz) throws Exception {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new Exception("在" + clazz.getName() + "中没有找到Entity注解，不能做ORM映射");
        }
        this.entityClass = clazz;
        Table table = clazz.getAnnotation(Table.class);
        tableName = table != null ? table.name() : clazz.getName();
        mappings = createMapping(clazz);
        rowMapper = createRowMapping();
        allColumn = mappings.keySet().toString().replaceAll("\\[|\\]", "");
    }

    private RowMapper<T> createRowMapping() {
        return new RowMapper<T>() {
            @Override
            public T mapRow(ResultSet resultSet, int i) throws SQLException {
                try {
                    T t = entityClass.getConstructor().newInstance();
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    for (int j = 0; j < columnCount; j++) {
                        Object value = resultSet.getObject(j);
                        String columnName = metaData.getColumnName(j);
                        fillBeanFieldValue(t, columnName, value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException();
                }
                return null;
            }
        };
    }

    private void fillBeanFieldValue(Object object, String columnName, Object value) {

    }

    private Map<String, PropertyMapping> createMapping(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        Field[] fields = ClassMappings.getFields(clazz);
        Map<String, Method> getters = ClassMappings.findPublicGetters(clazz);
        Map<String, Method> setter = ClassMappings.findPublicSetter(clazz);
        Map<String, PropertyMapping> map = new HashMap<>();
        String propertyName = null;
        for (Field field : fields) {
            propertyName = field.getName();
            if (propertyName.startsWith("is")) {
                propertyName = propertyName.substring(2);
            }
            if (getters.containsKey(propertyName) || setter.containsKey(propertyName)) {
                map.put(propertyName, new PropertyMapping(getters.get(propertyName), setter.get(propertyName), field));
            }
        }
        return map;
    }
}