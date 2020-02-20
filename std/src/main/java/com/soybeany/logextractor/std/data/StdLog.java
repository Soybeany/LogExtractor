package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.LinkedList;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class StdLog {

    public final String logId;
    public StdFlag startFlag;
    public StdFlag endFlag;
    public final LinkedList<StdLine> lines = new LinkedList<StdLine>();

    public StdLog(String logId) {
        this.logId = logId;
    }

    public String getType() {
        StdFlag flag = getFlag();
        if (null != flag) {
            return flag.type;
        }
        return StdFlag.TYPE_UNKNOWN;
    }

    public StdFlag getFlag() {
        if (null != startFlag) {
            return startFlag;
        }
        if (null != endFlag) {
            return endFlag;
        }
        return null;
    }

    public boolean isComplete() {
        return null != startFlag && null != endFlag;
    }
}
