package com.soybeany.logextractor.efb.old.data;

import com.soybeany.logextractor.efb.old.util.ToolUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class ExtractParam {

    // ****************************************KEY****************************************

    public static final String MANAGER_MODE = "manager_mode";

    public static final String HANDLER_LEVEL = "handler_level";
    public static final String HANDLER_TYPE = "handler_type";

    // ****************************************VALUE****************************************

    public static final String MODE_SYNC = "sync";
    public static final String MODE_ASYNC = "async";
    public static final String MODE_QUERY = "query";

    public static final String LEVEL_INFO = "info";
    public static final String LEVEL_WARN = "warn";
    public static final String LEVEL_ERROR = "error";

    public static final String TYPE_REQUEST = "request";
    public static final String TYPE_TIMER = "timer";
    public static final String TYPE_THREAD_POOL = "threadPool";

    // ****************************************容器****************************************

    public final Map<String, String> manager = new HashMap<String, String>();
    public final Map<String, String> loader = new HashMap<String, String>();
    public final Map<String, String> parser = new HashMap<String, String>();
    public final Map<String, String> handler = new HashMap<String, String>();
    public final Map<String, String> reporter = new HashMap<String, String>();

    // ****************************************默认设置****************************************

    {
        // 使用同步模式
        manager.put(MANAGER_MODE, MODE_SYNC);
        // 查询全部类型
        handler.put(HANDLER_TYPE, ToolUtils.arrToString(TYPE_REQUEST, TYPE_TIMER, TYPE_THREAD_POOL));
    }

}
