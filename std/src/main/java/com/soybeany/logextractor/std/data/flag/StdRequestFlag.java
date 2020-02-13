package com.soybeany.logextractor.std.data.flag;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class StdRequestFlag extends StdFlag {

    public String sender;
    public String url;
    public String param;

    public StdRequestFlag(StdFlag flag) {
        super(flag);
    }
}
