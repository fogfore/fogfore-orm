package com.fogfore.orm;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class QueryRuleSqlBuilder {
    private String whereSql;
    private String orderSql;
    private List<Object> values;

    private QueryRuleSqlBuilder(QueryRule queryRule) {
        if (queryRule == null) {
            return;
        }
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
        return whereSql != null && !whereSql.matches("^\\s*$");
    }

    public boolean hasOrderSql() {
        return orderSql != null && !orderSql.matches("^\\s*$");
    }

    public boolean hasValues() {
        return values != null && !values.isEmpty();
    }

    private void buildWhereSql(List<QueryRule.Rule> rules) {
        if (rules.isEmpty()) {
            return;
        }
        StringBuilder sql = new StringBuilder();
        for (QueryRule.Rule rule : rules) {
            sql.append(rule.getAndOr());
            sql.append(" ");
            sql.append(rule.getSql());

            if (values == null) {
                values = new LinkedList<>();
            }
            this.values.addAll(Arrays.asList(rule.getValues()));
        }
        this.whereSql = removeFirstSeparator(sql.toString());
    }

    private void buildOrderSql(List<QueryRule.Order> orders) {
        if (orders.isEmpty()) {
            return;
        }
        StringBuilder sql = new StringBuilder();
        for (QueryRule.Order order : orders) {
            sql.append(", ");
            sql.append(order.getPropertyName());
            sql.append(" ");
            sql.append(order.getType());
        }
        this.orderSql = removeFirstSeparator(sql.toString());
    }


    private String removeFirstSeparator(String str) {
        return str.replaceFirst("^\\s*(and|,)\\s*", "");
    }
}
