package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.std.data.flag.Flag;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class Log {

    public final String logId;
    public Flag startFlag;
    public Flag endFlag;
    public final List<Line> lines = new LinkedList<Line>();

    public Log(String logId) {
        this.logId = logId;
    }

    public String getType() {
        if (null != startFlag) {
            return startFlag.type;
        }
        if (null != endFlag) {
            return endFlag.type;
        }
        return Flag.TYPE_UNKNOWN;
    }

    public boolean isComplete() {
        return null != startFlag && null != endFlag;
    }
}
