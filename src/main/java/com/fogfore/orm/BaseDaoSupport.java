package com.fogfore.orm;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BaseDaoSupport<T extends Serializable, PK extends Serializable> {
    private EntityOperation<T> entityOperation;
    private JdbcTemplate jdbcTemplate;
    private DataSource dataSource;

    @SuppressWarnings("unchecked")
    public BaseDaoSupport() {
        try {
            Class<T> entityClass = GenericsUtils.getSuperClassGenericType(getClass());
            this.entityOperation = new EntityOperation<T>(entityClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> parse(T t) {
        if (ObjectUtils.isEmpty(t)) {
            return null;
        }
        return entityOperation.parse(t);
    }

    public String getTableName() {
        return this.entityOperation.getTableName();
    }

    public T select(PK id) {
        return null;
    }

    public List<T> selectAll() {
        return null;
    }

    public boolean insert(T t) {
        return doInsert(t);
    }

    public PK insertAndReturnId(T t) {
        return null;
    }

    public boolean insertAll(List<T> list) {
        return false;
    }

    public boolean update(T t) {
        return false;
    }

    public boolean updateAll(List<T> list) {
        return false;
    }

    public boolean delete(T t) {
        return false;
    }

    public boolean deleteAll(List<T> list) {
        return false;
    }

    private boolean doInsert(T t) {
        Map<String, Object> parse = entityOperation.parse(t);
        return false;
    }
}
