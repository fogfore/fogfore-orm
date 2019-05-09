package com.fogfore.orm;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class MainTest {
    @Test
    public void test() {
        Set<String> set = new HashSet<>();
        set.add("wo");
        set.add("men");
        set.add("shi");
        set.add("yi");
        set.add("jia");
        set.add("ren");
        System.out.println(set.toString().replaceAll("\\[|\\]", ""));
    }
}
