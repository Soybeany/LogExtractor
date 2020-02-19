package com.soybeany.logextractor.efb.util;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class TypeChecker {

    public static boolean isRequest(String type) {
        return "客户端".equals(type) || "管理端".equals(type);
    }

}
