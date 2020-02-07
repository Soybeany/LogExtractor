package com.soybeany.std.data;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class Report {

    public int expectCount;
    public int actualCount;
    public String endReason;

    public String lastDataId;
    public String curDataId;
    public String nextDataId;

    public long startPointer;
    public long endPointer;

    public final List<Log> logs = new LinkedList<Log>();
}
