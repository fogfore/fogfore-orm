package com.fogfore.orm;

import com.fogfore.utils.GenericsUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class BaseDaoSupport<T extends Serializable, PK extends Serializable> {
    private EntityOperation<T> entityOperation;
    private JdbcTemplate jdbcTemplateRead;
    private JdbcTemplate jdbcTemplateWrite;
    private DataSource dataSourceRead;
    private DataSource dataSourceWrite;

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

    private boolean doInsertAndReturnId(T t) {
        return false;
    }

    private boolean doInsert(T t) {
        Map<String, Object> param = entityOperation.parse(t);
        String sql = makeSimpleInsertSql(this.getTableName(), param);
        int result = this.jdbcTemplateWrite.update(sql, param.values().toArray());
        return result > 0;
    }

    private String makeSimpleInsertSql(String tableName, Map<String, Object> param) {
        if (StringUtils.isEmpty(tableName) || ObjectUtils.isEmpty(param)) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(tableName);
        StringJoiner columnNames = new StringJoiner(",", "(", ")");
        StringJoiner values = new StringJoiner(",", "(", ")");
        for (String columnName : param.keySet()) {
            columnNames.add(columnName);
            values.add("?");
        }
        sb.append(columnNames.toString());
        sb.append(" values ");
        sb.append(values.toString());
        sb.append(";");
        return sb.toString();
    }

    protected void setDataSourceRead(DataSource dataSourceRead) {
        this.dataSourceRead = dataSourceRead;
        this.jdbcTemplateRead = new JdbcTemplate(dataSourceRead);
    }

    protected void setDataSourceWrite(DataSource dataSourceWrite) {
        this.dataSourceWrite = dataSourceWrite;
        this.jdbcTemplateWrite = new JdbcTemplate(dataSourceWrite);
    }

    protected void setDataSource(DataSource dataSource) {
        setDataSourceRead(dataSource);
        setDataSourceWrite(dataSource);
    }

    public DataSource getDataSource() {
        return this.dataSourceRead;
    }
}
