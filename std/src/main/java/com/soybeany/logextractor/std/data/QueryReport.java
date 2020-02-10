package com.soybeany.logextractor.std.data;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class QueryReport extends ScanReport {

    public int expectCount;
    public int actualCount;
    public String endReason;

    public List<Log> logs;

    public String lastDataId;
    public String curDataId;
    public String nextDataId;

    public Long startPointer;
    public Long endPointer;

}
