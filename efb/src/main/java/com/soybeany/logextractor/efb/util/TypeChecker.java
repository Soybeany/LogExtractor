package com.soybeany.logextractor.efb.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class TypeChecker {

    private static final String TYPE_CLIENT = "客户端";
    private static final String TYPE_MANAGE = "管理端";
    private static final String TYPE_TIMER = "定时器";

    public static final Set<String> ALL_TYPES = new HashSet<String>(Arrays.asList(TYPE_CLIENT, TYPE_MANAGE, TYPE_TIMER));

    public static boolean isRequest(String type) {
        return TYPE_CLIENT.equals(type) || TYPE_MANAGE.equals(type);
    }

}
