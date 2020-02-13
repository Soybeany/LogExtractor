package com.soybeany.logextractor.std.data.flag;

import com.soybeany.logextractor.std.data.StdMetaInfo;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class StdFlag {

    public static final String STATE_START = "开始";
    public static final String STATE_END = "结束";

    public static final String TYPE_UNKNOWN = "未知";

    public final StdMetaInfo info;
    public String state;
    public String type;

    public StdFlag(StdMetaInfo info) {
        this.info = info;
    }

    public StdFlag(StdFlag flag) {
        this(flag.info);
        state = flag.state;
        type = flag.type;
    }

}
