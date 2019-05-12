package com.fogfore.orm;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.lang.reflect.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class EntityOperation<T> {
    private final Class<T> entityClass;
    private final String tableName;
    private final Map<String, PropertyMapping> propertyMappingMap;
    private final RowMapper<T> rowMapper;
    private final String allColumn;
    private final Field pkField;

    public EntityOperation(Class<T> clazz) throws Exception {
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new Exception("在" + clazz.getName() + "中没有找到Entity注解，不能做ORM映射");
        }
        this.entityClass = clazz;
        Table table = clazz.getAnnotation(Table.class);
        tableName = table != null ? table.name() : clazz.getName();
        propertyMappingMap = createPropertyMappingMap(clazz);
        rowMapper = createRowMapping();
        allColumn = CollectionUtils.isEmpty(propertyMappingMap) ? "*" :
                propertyMappingMap.keySet().toString().replaceAll("\\[|\\]", "");
        pkField = findPkField(clazz);
    }

    private Field findPkField(Class<T> clazz) {
        if (ObjectUtils.isEmpty(clazz)) {
            return null;
        }
        Field[] fields = ClassMappings.getFields(clazz);
        for (Field field : fields) {
            if (field.isAnnotationPresent(Id.class) || StringUtils.equals("id", field.getName())) {
                return field;
            }
        }
        return null;
    }

    public Class getPkClass() {
        return pkField.getType();
    }

    public Object parse(ResultSet resultSet) {
        if (ObjectUtils.isEmpty(resultSet)) {
            return null;
        }
        T t = null;
        try {
            t = this.rowMapper.mapRow(resultSet, 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return t;
    }

    public Map<String, Object> parse(T t) {
        if (ObjectUtils.isEmpty(t) || propertyMappingMap == null || propertyMappingMap.isEmpty()) {
            return null;
        }
        Map<String, Object> map = new TreeMap<>();
        try {
            for (Map.Entry<String, PropertyMapping> entry : propertyMappingMap.entrySet()) {
                String columnName = entry.getKey();
                PropertyMapping propertyMapping = entry.getValue();
                Object obj = null;
                obj = propertyMapping.get(t);
                map.put(columnName, obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void println(T t) {
        try {
            for (Map.Entry<String, PropertyMapping> entry : propertyMappingMap.entrySet()) {
                String columnName = entry.getKey();
                Object value = entry.getValue().get(t);
                System.out.println(columnName + " = " + value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    return t;
                } catch (Exception e) {
                    throw new SQLException(e);
                }
            }
        };
    }

    private void fillBeanFieldValue(Object object, String columnName, Object value) {
        if (ObjectUtils.isEmpty(object) || StringUtils.isEmpty(columnName) || ObjectUtils.isEmpty(object)) {
            return;
        }
        PropertyMapping pm = propertyMappingMap.get(columnName);
        if (pm != null) {
            try {
                pm.set(object, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * columnName存在，key为columnName，否则为fieldName
     *
     * @param clazz
     * @return
     */
    private Map<String, PropertyMapping> createPropertyMappingMap(Class<T> clazz) {
        if (ObjectUtils.isEmpty(clazz)) {
            return null;
        }
        Field[] fields = ClassMappings.getFields(clazz);
        Map<String, Method> getters = ClassMappings.findPublicGetters(clazz);
        Map<String, Method> setters = ClassMappings.findPublicSetter(clazz);
        Map<String, PropertyMapping> map = new HashMap<>();
        String propertyName = null;
        for (Field field : fields) {
            if (field.isAnnotationPresent(Transient.class)) {
                continue;
            }
            Column column = field.getAnnotation(Column.class);

            propertyName = field.getName().startsWith("is") ? field.getName().substring(2) : field.getName();
            propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
            propertyName = !ObjectUtils.isEmpty(column) && !StringUtils.isEmpty(column.name()) ?
                    column.name() : propertyName;

            Method getter = getters.get(propertyName);
            Method setter = setters.get(propertyName);
            if (getter == null || setter == null) {
                continue;
            }

            map.put(propertyName, new PropertyMapping(getter, setter, field));
        }
        return map;
    }

    public String getTableName() {
        return tableName;
    }

    public RowMapper<T> getRowMapper() {
        return rowMapper;
    }

    public String getAllColumn() {
        return allColumn;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Map<String, PropertyMapping> getPropertyMappingMap() {
        return propertyMappingMap;
    }

    public Field getPkField() {
        return pkField;
    }
}
