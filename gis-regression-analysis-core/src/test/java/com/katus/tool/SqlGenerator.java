package com.katus.tool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SUN Katus
 * @version 1.0, 2021-10-05
 */
public class SqlGenerator {

    public static void main(String[] args) {
        List<String> sqlList = new ArrayList<>();
        for (String arg : args) {
            sqlList.add(String.format("SGSF LIKE '%%%s%%'", arg));
        }
        sqlList.forEach(sql -> {
            System.out.println(sql + " OR ");
        });
    }
}
