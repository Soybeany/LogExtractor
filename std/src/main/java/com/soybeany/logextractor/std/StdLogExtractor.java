package com.soybeany.logextractor.std;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.sfile.SFileLogExtractor;
import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.loader.SingleFileLoader;
import com.soybeany.logextractor.std.Loader.StdFileLoader;
import com.soybeany.logextractor.std.data.IStdData;
import com.soybeany.logextractor.std.data.Line;
import com.soybeany.logextractor.std.data.Log;
import com.soybeany.logextractor.std.data.QueryReport;
import com.soybeany.logextractor.std.data.flag.Flag;

/**
 * 标准日志提取器
 * <br>提供标准的报告内容
 * <br>Created by Soybeany on 2020/2/8.
 */
public class StdLogExtractor<Index extends ISFileIndex, Report extends QueryReport, Data extends IStdData<Index, Report>> extends SFileLogExtractor<Index, Line, Flag, Log, Report, Data> {

    // ****************************************设置方法****************************************

    @Override
    public void setLoader(SingleFileLoader<Index, Data> loader) {
        throw new BusinessException("不支持此方法，请使用StdFileLoader入参的重载");
    }

    public void setLoader(StdFileLoader<Index, Data> loader) {
        super.setLoader(loader);
    }

}
