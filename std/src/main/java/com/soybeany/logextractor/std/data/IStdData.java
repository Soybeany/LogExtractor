package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.sfile.data.ISFileData;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public interface IStdData<Index, Report> extends ISFileData<Index, Report>, ILogStorageIdAccessor, IReportInfo {
}
