package com.fogfore.orm;

import com.fogfore.utils.GenericsUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
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

    public Number insertAndReturnId(T t) throws SQLException {
        Class pkClass = entityOperation.getPkClass();
        if (pkClass != Integer.class && pkClass != int.class &&
                pkClass != Long.class && pkClass != long.class) {
            throw new SQLException("不支持主键类型" + pkClass);
        }
        return doInsertAndReturnId(t);
    }

    public boolean insertAll(List<T> list) {
        return doInsertAll(list);
    }

    public boolean update(T t) {
        return false;
    }

    public boolean updateAll(List<T> list) {
        return false;
    }

    public boolean delete(T t) throws Exception {
        return doDelete(t);
    }

    public boolean deleteAll(List<T> list) throws Exception {
        return doDeleteAll(list);
    }

    public boolean deleteByPk(PK pkValue) {
        return doDeleteByPk(pkValue);
    }

    public boolean deleteAllByPk(List<PK> list) {
        return deleteAllByPk(list);
    }

    @SuppressWarnings("unchecked")
    private boolean doDelete(T t) throws Exception {
        if (ObjectUtils.isEmpty(t)) {
            return false;
        }
        PK pkValue = (PK) entityOperation.getPkValue(t);
        return doDeleteByPk(pkValue);
    }

    @SuppressWarnings("unchecked")
    private boolean doDeleteAll(List<T> list) throws Exception {
        if (ObjectUtils.isEmpty(list)) {
            return false;
        }
        List<PK> pkList = new LinkedList<>();
        for (T t : list) {
            PK pkValue = (PK) entityOperation.getPkValue(t);
            pkList.add(pkValue);
        }
        return doDeleteAllByPk(pkList);
    }

    private boolean doDeleteByPk(PK pkValue) {
        if (ObjectUtils.isEmpty(pkValue)) {
            return false;
        }
        String sql = makeSimpleDeleteSql(entityOperation.getPkName());
        int result = jdbcTemplateWrite.update(sql, pkValue);
        return result > 0;
    }

    private boolean doDeleteAllByPk(List<PK> list) {
        if (ObjectUtils.isEmpty(list)) {
            return false;
        }
        String sql = makeDeleteSql(entityOperation.getPkName(), list.size());
        int result = jdbcTemplateWrite.update(sql, list.toArray());
        return result == list.size();
    }

    private boolean doInsert(T t) {
        if (ObjectUtils.isEmpty(t)) {
            return false;
        }
        Map<String, Object> param = entityOperation.parse(t);
        String sql = makeSimpleInsertSql(this.getTableName(), param.keySet().toArray());
        int result = this.jdbcTemplateWrite.update(sql, param.values().toArray());
        return result > 0;
    }

    private Number doInsertAndReturnId(T t) {
        if (ObjectUtils.isEmpty(t)) {
            return null;
        }
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        Map<String, Object> param = entityOperation.parse(t);
        String sql = makeSimpleInsertSql(getTableName(), param.keySet().toArray());
        jdbcTemplateWrite.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                Object[] objects = param.values().toArray();
                for (int i = 0; i < objects.length; i++) {
                    ps.setObject(i + 1, objects[i]);
                }
                return ps;
            }
        }, keyHolder);
        Number key = keyHolder.getKey();
        return keyHolder.getKey();
    }

    private boolean doInsertAll(List<T> list) {
        if (ObjectUtils.isEmpty(list)) {
            return false;
        }
        Object[] columnNames = entityOperation.getPropertyMappingMap().keySet().toArray();
        LinkedList<Object> values = new LinkedList<>();
        String sql = makeInsertSql(getTableName(), columnNames, list.size());
        for (T t : list) {
            Map<String, Object> map = entityOperation.parse(t);
            values.addAll(map.values());
        }
        int result = jdbcTemplateWrite.update(sql, values.toArray());
        return result == list.size();
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

    private String makeSimpleInsertSql(String tableName, Object[] columnNames) {
        return makeInsertSql(tableName, columnNames, 1);
    }

    private String makeInsertSql(String tableName, Object[] columnNames, int rowNum) {
        if (StringUtils.isEmpty(tableName) || ObjectUtils.isEmpty(columnNames) || rowNum < 1) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ");
        sb.append(tableName);
        sb.append(" ");
        StringJoiner namesJoiner = new StringJoiner(",", "(", ")");
        StringJoiner valuesJoiner = new StringJoiner(",", "(", ")");
        for (Object name : columnNames) {
            if (name instanceof String) {
                namesJoiner.add((String) name);
                valuesJoiner.add("?");
            }
        }
        sb.append(namesJoiner.toString());
        sb.append(" values ");
        StringJoiner multiJoiner = new StringJoiner(",", "", "");
        for (int i = 0; i < rowNum; i++) {
            multiJoiner.add(valuesJoiner.toString());
        }
        sb.append(multiJoiner.toString());
        sb.append(";");
        return sb.toString();
    }

    private String makeSimpleDeleteSql(String pkName) {
        return makeDeleteSql(pkName, 1);
    }

    private String makeDeleteSql(String pkName, int rowNum) {
        if (StringUtils.isEmpty(pkName) || rowNum < 1) {
            return null;
        }
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ");
        sql.append(getTableName());
        sql.append(" where ");
        sql.append(pkName);
        sql.append(" ");
        sql.append(QueryRule.IN);
        sql.append(" ");
        StringJoiner condition = new StringJoiner(",", "(", ")");
        for (int i = 0; i < rowNum; i++) {
            condition.add("?");
        }
        sql.append(condition);
        return sql.toString();
    }
}
