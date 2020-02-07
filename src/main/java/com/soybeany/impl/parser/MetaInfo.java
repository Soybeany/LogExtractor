package com.soybeany.impl.parser;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public class MetaInfo {
    public String time;
    public String level;
    public String thread;
    public String position;


    public String getLogId() {
        return thread;
    }
}
