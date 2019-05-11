package com.fogfore.orm;


import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class QueryRuleSqlBuilder {
    private String whereSql;
    private String orderSql;
    private List<Object> values;

    private QueryRuleSqlBuilder(QueryRule queryRule) {
        if (queryRule.hasRules()) {
            buildWhereSql(queryRule.getRules());
        }
        if (queryRule.hasOrders()) {
            buildOrderSql(queryRule.getOrders());
        }
    }

    public static QueryRuleSqlBuilder build(QueryRule queryRule) {
        if (queryRule == null) {
            return null;
        }
        return new QueryRuleSqlBuilder(queryRule);
    }

    public boolean hasWhereSql() {
        return StringUtils.isEmpty(whereSql);
    }

    public boolean hasOrderSql() {
        return StringUtils.isEmpty(orderSql);
    }

    public boolean hasValues() {
        return ObjectUtils.isEmpty(values);
    }

    private void buildWhereSql(List<QueryRule.Rule> rules) {
        if (ObjectUtils.isEmpty(rules)) {
            return;
        }
        StringBuilder sql = new StringBuilder();
        boolean flag = false;
        for (QueryRule.Rule rule : rules) {
            if (flag) {
                sql.append(" ");
                sql.append(rule.getAndOr());
                sql.append(" ");
            }
            flag = true;
            sql.append(rule.getSql());

            if (values == null) {
                values = new LinkedList<>();
            }
            this.values.addAll(Arrays.asList(rule.getValues()));
        }
        this.whereSql = sql.toString();
    }

    private void buildOrderSql(List<QueryRule.Order> orders) {
        if (ObjectUtils.isEmpty(orders)) {
            return;
        }
        StringJoiner joiner = new StringJoiner(",", "", "");
        orders.forEach(order -> {
            joiner.add(order.getSql());
        });
        this.orderSql = joiner.toString();
    }
}
