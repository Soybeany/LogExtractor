package com.soybeany.log.util;

import com.soybeany.log.base.ParamException;

import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class ToolUtils {

    // ****************************************检查****************************************

    public static void checkExist(Map<String, String> param, String... keys) throws ParamException {
        if (null == keys || keys.length == 0) {
            return;
        }
        for (String key : keys) {
            if (isEmpty(param.get(key))) {
                throw new ParamException("缺失必要的参数(" + key + ")");
            }
        }
    }

    public static boolean isEmpty(String string) {
        return null == string || string.trim().length() == 0;
    }

    // ****************************************转换****************************************

    public static String arrToString(String... item) {
        if (null == item || item.length == 0) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(item[0]);
        for (int i = 1; i < item.length; i++) {
            builder.append("|").append(item[i]);
        }
        return builder.toString();
    }

    public static String[] stringToArr(String string) {
        if (null == string) {
            return null;
        }
        return string.split("\\|");
    }

}
