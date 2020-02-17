package com.soybeany.logextractor.std.data;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdReport {

    public int expectCount;
    public int actualCount;
    public String noNextDataReason;

    public Long totalScan;
    public Long newScan;
    public Long queryLoad;

    public String lastDataId;
    public String curDataId;
    public String nextDataId;

    public List<StdLog> logs;
}
