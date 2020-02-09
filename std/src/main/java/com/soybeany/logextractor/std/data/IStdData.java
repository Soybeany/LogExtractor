package com.soybeany.logextractor.std.data;

import java.util.List;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/8.
 */
public interface IStdData {

    Map<String, Log> getLogMap();

    List<Log> getLogList();

    int getLogLimit();


}
