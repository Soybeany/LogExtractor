package com.soybeany.logextractor.std.data;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public class StdMetaInfo {
    public String time;
    public String level;
    public String thread;
    public String position;

    public String getLogId() {
        return thread;
    }
}
