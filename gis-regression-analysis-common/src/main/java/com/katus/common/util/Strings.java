package com.katus.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 字符串操作类
 *
 * @author SUN Katus
 * @version 1.0, 2021-05-31
 */
public final class Strings {
    /**
     * 字符串有值且不为空字符串
     *
     * @param origin 检验字符串
     * @return 是否有值且不为空字符串
     */
    public static boolean hasLength(String origin) {
        return origin != null && !origin.isEmpty();
    }

    /**
     * 将字符串分割转换为List
     * 被""包裹的部分作为一个整体考虑
     *
     * @param origin    原始字符串
     * @param separator 分隔符
     * @return 分割后的字符串列表
     */
    public static List<String> splitToList(String origin, String separator) {
        String[] items = origin.split(separator + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        return new ArrayList<>(Arrays.asList(items));
    }

    /**
     * 将字符串转换为不含空或者空格字符串的List
     * 被""包裹的部分作为一个整体考虑
     *
     * @param origin    原始字符串
     * @param separator 分隔符
     * @return 分割后的字符串列表
     */
    public static List<String> splitToListWithoutEmpty(String origin, String separator) {
        List<String> itemList = new ArrayList<>();
        String[] items = origin.split(separator + "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        for (String item : items) {
            String finalItem = item.trim();
            if (!finalItem.isEmpty()) {
                itemList.add(finalItem);
            }
        }
        return itemList;
    }

    /***
     * 去除字符串后缀
     * @param origin    原始字符串
     * @param tail      待去除后缀
     * @return 去尾缀字符串
     */
    public static String trimEnd(String origin, String tail) {
        if (origin.endsWith(tail)) {
            return origin.substring(0, origin.length() - tail.length());
        } else {
            return origin;
        }
    }

    /***
     * 去除字符串前缀
     * @param origin    原始字符串
     * @param head      待去除前缀
     * @return 去首缀字符串
     */
    public static String trimHead(String origin, String head) {
        if (origin.startsWith(head)) {
            return origin.substring(head.length());
        } else {
            return origin;
        }
    }

    /***
     * 去除字符串前后引用字符
     * @param origin    原始字符串
     * @param Quotechar 引用字符
     * @return 去引用字符串
     */
    public static String trimQuotechar(String origin, String Quotechar) {
        if (origin.startsWith(Quotechar) & origin.endsWith(Quotechar)) {
            return origin.substring(Quotechar.length(), origin.length() - Quotechar.length());
        } else {
            return origin;
        }
    }

    /**
     * 生成全局唯一UUID字符串
     * @return UUID字符串
     */
    public static synchronized String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
