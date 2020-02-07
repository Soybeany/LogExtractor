package com.soybeany.std.data;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public interface IReportData {

    List<Log> getLogList();

    int getLimitCount();

    String getLastDataId();

    String getCurDataId();

    String getNextDataId();

}
