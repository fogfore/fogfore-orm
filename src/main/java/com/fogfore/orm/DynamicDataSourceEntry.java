package com.fogfore.orm;

import org.aopalliance.intercept.Joinpoint;

public class DynamicDataSourceEntry {
    public static final String DEFAULT_SOURCE = null;
    private static final ThreadLocal<String> LOCAL = new ThreadLocal<>();

    public void set(String source) {
        LOCAL.set(source);
    }

    public String get() {
        return LOCAL.get();
    }

    public void clear() {
        LOCAL.remove();
    }

    public void restore() {
        LOCAL.set(DEFAULT_SOURCE);
    }

    public void restore(Joinpoint joinpoint) {
        LOCAL.set(DEFAULT_SOURCE);
    }

    public void set(int year) {
        LOCAL.set("db_" + year);
    }

}
