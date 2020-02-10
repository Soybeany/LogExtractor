package com.soybeany.logextractor.std.data;

import com.soybeany.logextractor.sfile.data.IRenewalInfoAccessor;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public interface IReportInfoProvider extends IRenewalInfoAccessor, ILoadDataAccessor {

    int getLogLimit();

}
