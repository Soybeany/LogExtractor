package com.soybeany.logextractor.std;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.common.IInstanceFactory;
import com.soybeany.logextractor.sfile.SFileLogExtractor;
import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.data.ISFileParam;
import com.soybeany.logextractor.sfile.loader.SingleFileLoader;
import com.soybeany.logextractor.std.Loader.StdFileLoader;
import com.soybeany.logextractor.std.data.Line;
import com.soybeany.logextractor.std.data.Log;
import com.soybeany.logextractor.std.data.StdData;
import com.soybeany.logextractor.std.data.flag.Flag;

/**
 * 标准日志提取器
 * <br>提供标准的报告内容
 * <br>Created by Soybeany on 2020/2/8.
 */
public class StdLogExtractor<Param extends ISFileParam, Index extends ISFileIndex, Report, Data extends StdData<Param, Index, Report>> extends SFileLogExtractor<Param, Index, Line, Flag, Log, Report, Data> {

    public StdLogExtractor(Class<Data> dataClass, Class<Index> indexClass) {
        this(new DefaultInstanceFactory<Data>(dataClass), new DefaultInstanceFactory<Index>(indexClass));
    }

    public StdLogExtractor(IInstanceFactory<Data> dataFactory, IInstanceFactory<Index> indexFactory) {
        super(dataFactory, indexFactory);
    }

    // ****************************************设置方法****************************************

    @Override
    public void setLoader(SingleFileLoader<Param, Index, Data> loader) {
        if (!(loader instanceof StdFileLoader)) {
            throw new BusinessException("请使用StdFileLoader作为入参");
        }
        super.setLoader(loader);
    }

    // ****************************************内部类****************************************

    private static class DefaultInstanceFactory<T> implements IInstanceFactory<T> {
        private Class<T> mClazz;

        DefaultInstanceFactory(Class<T> clazz) {
            mClazz = clazz;
        }

        @Override
        public T getNew() {
            try {
                return mClazz.newInstance();
            } catch (Exception e) {
                throw new BusinessException("无法创建新的数据实例:" + e.getMessage());
            }
        }
    }

}