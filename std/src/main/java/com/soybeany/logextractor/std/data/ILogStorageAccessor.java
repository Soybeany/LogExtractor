package com.soybeany.logextractor.std.data;

import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public interface ILogStorageAccessor {

    Map<String, Log> getLogStorage();

    void setLogStorage(Map<String, Log> storage);

}
