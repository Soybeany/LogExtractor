package com.soybeany.std.data.flag;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class RequestFlag extends Flag {

    public String sender;
    public String url;
    public String param;

    public RequestFlag(Flag flag) {
        super(flag);
    }
}
