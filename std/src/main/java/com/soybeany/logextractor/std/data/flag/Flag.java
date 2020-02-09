package com.soybeany.logextractor.std.data.flag;

import com.soybeany.logextractor.std.data.MetaInfo;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class Flag {

    public static final String STATE_START = "开始";
    public static final String STATE_END = "结束";

    public static final String TYPE_UNKNOWN = "未知";

    public final MetaInfo info;
    public String state;
    public String type;

    public Flag(MetaInfo info) {
        this.info = info;
    }

    public Flag(Flag flag) {
        this(flag.info);
        state = flag.state;
        type = flag.type;
    }

}
