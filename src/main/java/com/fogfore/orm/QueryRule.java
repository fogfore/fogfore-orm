package com.fogfore.orm;

import java.io.Serializable;
import java.util.*;

public final class QueryRule implements Serializable {

    private static final long serialVersionUID = -7283455880862372435L;
    public static final String AND = "and";
    public static final String OR = "or";
    public static final String NOT = "not";
    public static final String BETWEEN = "between";
    public static final String IN = "in";
    public static final String LIKE = "like";
    public static final String IS_NULL = "is null";
    public static final String IS_NOT_NULL = "is not null";
    public static final String EQUAL = "=";
    public static final String NOT_EQUAL = "<>";
    public static final String LESS_THAN = "<";
    public static final String MORE_THAN = ">";
    public static final String LESS_OR_EQUAL = "<=";
    public static final String MORE_OR_EQUAL = ">=";
    public static final String DESC = "desc";
    public static final String ASC = "asc";


    private List<Rule> rules;
    private List<Order> orders;

    private QueryRule() {
        rules = new LinkedList<>();
    }

    public static QueryRule newInstance() {
        return new QueryRule();
    }

    public QueryRule andDesc(String propertyName) {
        addOrder(propertyName, DESC);
        return this;
    }

    public QueryRule andAsc(String propertyName) {
        addOrder(propertyName, ASC);
        return this;
    }

    public QueryRule andBetween(String propertyName, Object[] values) {
        addRule(AND, propertyName, BETWEEN, "", surroundBySpaces(AND), "", values);
        return this;
    }

    public QueryRule andIsNull(String propertyName) {
        addRule(AND, propertyName, IS_NULL);
        return this;
    }

    public QueryRule andIsNotNull(String propertyName) {
        addRule(AND, propertyName, IS_NOT_NULL);
        return this;
    }

    public QueryRule andEqual(String propertyName, Object value) {
        addRule(AND, propertyName, EQUAL, value);
        return this;
    }

    public QueryRule andNotEqual(String propertyName, Object value) {
        addRule(AND, propertyName, NOT_EQUAL, value);
        return this;
    }

    public QueryRule andLessThen(String propertyName, Object value) {
        addRule(AND, propertyName, LESS_THAN, value);
        return this;
    }

    public QueryRule andLessEqual(String propertyName, Object value) {
        addRule(AND, propertyName, LESS_OR_EQUAL, value);
        return this;
    }

    public QueryRule andMoreThen(String propertyName, Object value) {
        addRule(AND, propertyName, MORE_THAN, value);
        return this;
    }

    public QueryRule andMoreEqual(String propertyName, Object value) {
        addRule(AND, propertyName, MORE_OR_EQUAL, value);
        return this;
    }

    public QueryRule andIn(String propertyName, Object[] values) {
        addRule(AND, propertyName, IN, "(", ",", ")", values);
        return this;
    }

    public QueryRule andNotIn(String propertyName, Object[] values) {
        addRule(AND, propertyName, NOT + " " + IN, "(", ",", ")", values);
        return this;
    }

    public QueryRule andLike(String propertyName, Object value) {
        addRule(AND, propertyName, LIKE, value);
        return this;
    }

    public QueryRule orBetween(String propertyName, Object[] values) {
        addRule(OR, propertyName, BETWEEN, "", surroundBySpaces(AND), "", values);
        return this;
    }

    public QueryRule orIsNull(String propertyName) {
        addRule(OR, propertyName, IS_NULL);
        return this;
    }

    public QueryRule orIsNotNull(String propertyName) {
        addRule(OR, propertyName, IS_NOT_NULL);
        return this;
    }

    public QueryRule orEqual(String propertyName, Object value) {
        addRule(OR, propertyName, EQUAL, value);
        return this;
    }

    public QueryRule orNotEqual(String propertyName, Object value) {
        addRule(OR, propertyName, NOT_EQUAL, value);
        return this;
    }

    public QueryRule orLessThen(String propertyName, Object value) {
        addRule(OR, propertyName, LESS_THAN, value);
        return this;
    }

    public QueryRule orLessEqual(String propertyName, Object value) {
        addRule(OR, propertyName, LESS_OR_EQUAL, value);
        return this;
    }

    public QueryRule orMoreThen(String propertyName, Object value) {
        addRule(OR, propertyName, MORE_THAN, value);
        return this;
    }

    public QueryRule orMoreEqual(String propertyName, Object value) {
        addRule(OR, propertyName, MORE_OR_EQUAL, value);
        return this;
    }

    public QueryRule orIn(String propertyName, Object[] values) {
        addRule(OR, propertyName, IN, "(", ",", ")", values);
        return this;
    }

    public QueryRule orNotIn(String propertyName, Object[] values) {
        addRule(OR, propertyName, NOT + " " + IN, "(", ",", ")", values);
        return this;
    }

    public QueryRule orLike(String propertyName, Object value) {
        addRule(OR, propertyName, LIKE, value);
        return this;
    }

    private void addRule(String andOr, String propertyName, String symbol) {
        addRule(andOr, propertyName, symbol, "", "", "");
    }

    private void addRule(String andOr, String propertyName, String symbol, Object... values) {
        addRule(andOr, propertyName, symbol, "", "", "", values);
    }

    private void addRule(String andOr, String propertyName, String symbol, String prefix, String split, String suffix, Object... values) {
        StringJoiner joiner = new StringJoiner(split, prefix, suffix);
        for (Object value : values) {
            joiner.add(" ? ");
        }
        String sql = propertyName + " " + symbol + " " + joiner.toString();
        rules.add(new Rule(andOr, sql, values));
    }

    private void addOrder(String propertyName, String type) {
        orders.add(new Order(propertyName, type));
    }

    private String surroundBySpaces(String str) {
        str = str.trim();
        str = " " + str + " ";
        return str;
    }

    final class Rule {
        private String sql;
        private Object[] values;
        private String andOr;

        Rule(String andOr, String sql, Object... values) {
            this.andOr = andOr;
            this.sql = sql;
            this.values = values;
        }

        public void setAndOr(String andOr) {
            this.andOr = andOr;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public Object[] getValues() {
            return values;
        }

        public void setValues(Object[] values) {
            this.values = values;
        }

        public String getAndOr() {
            return andOr;
        }

        @Override
        public String toString() {
            return "Rule{" +
                    "sql='" + sql + '\'' +
                    ", values=" + values +
                    ", andOr='" + andOr + '\'' +
                    '}';
        }
    }

    final class Order {
        private String propertyName;
        private String type;

        public Order(String propertyName, String type) {
            this.propertyName = propertyName;
            this.type = type;
        }

        public String getPropertyName() {
            return propertyName;
        }

        public void setPropertyName(String propertyName) {
            this.propertyName = propertyName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Order{" +
                    "propertyName='" + propertyName + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }


    public List<Rule> getRules() {
        return rules;
    }

    public List<Order> getOrders() {
        return orders;
    }
}