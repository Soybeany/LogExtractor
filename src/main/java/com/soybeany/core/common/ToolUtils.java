package com.soybeany.core.common;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class ToolUtils {

    public static void checkNull(Object obj, String msg) {
        if (null == obj) {
            throw new RuntimeException(msg);
        }
    }

}