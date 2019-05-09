package com.fogfore.orm;

import com.fogfore.orm.QueryRule;

public class QueryRuleSqlBuilder {


    public String build(QueryRule queryRule) {
        if (queryRule == null || queryRule.getRules().isEmpty()) {
            return null;
        }
        StringBuilder sql = new StringBuilder();
        for (QueryRule.Rule rule : queryRule.getRules()) {
            sql.append(rule.getAndOr());
            sql.append(" ");
            sql.append(rule.getSql());
        }
        return sql.toString();

    }


}