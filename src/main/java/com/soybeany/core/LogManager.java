package com.soybeany.core;

import com.soybeany.core.common.BaseIndexCenter;
import com.soybeany.core.common.BaseLoader;
import com.soybeany.core.common.BaseStorageCenter;
import com.soybeany.core.common.DataIdException;
import com.soybeany.core.index.BaseCreatorFactory;
import com.soybeany.core.index.IndexManager;
import com.soybeany.core.sort.BaseFilterFactory;
import com.soybeany.core.sort.BaseLogFactory;
import com.soybeany.core.sort.BaseReporter;
import com.soybeany.core.sort.SortManager;
import com.soybeany.core.sort.parser.BaseFlagParser;
import com.soybeany.core.sort.parser.BaseLineParser;

import java.io.IOException;

/**
 * 日志管理器(Facade)
 * <br>Created by Soybeany on 2020/2/7.
 */
public class LogManager<Data, Range, Index, RLine, Line, Flag, Log, Report> {

    private IndexManager<Data, Range, Index, RLine, Line, Flag> mIndexManager = new IndexManager<Data, Range, Index, RLine, Line, Flag>();
    private SortManager<Data, Range, Index, RLine, Line, Flag, Log, Report> mSortManager = new SortManager<Data, Range, Index, RLine, Line, Flag, Log, Report>();

    // ****************************************设置API****************************************

    public void setIndexCenter(BaseIndexCenter<Data, Range, Index> center) {
        mIndexManager.setIndexCenter(center);
        mSortManager.setIndexCenter(center);
    }

    public void setLoader(BaseLoader<Data, Range, RLine> loader) {
        mIndexManager.setLoader(loader);
        mSortManager.setLoader(loader);
    }

    public void setLineParser(BaseLineParser<Data, RLine, Line> parser) {
        mIndexManager.setLineParser(parser);
        mSortManager.setLineParser(parser);
    }

    public void setFlagParser(BaseFlagParser<Data, Line, Flag> parser) {
        mIndexManager.setFlagParser(parser);
        mSortManager.setFlagParser(parser);
    }

    public void setCreatorFactory(BaseCreatorFactory<Data, Index, RLine, Line, Flag> factory) {
        mIndexManager.setCreatorFactory(factory);
    }

    public void setStorageCenter(BaseStorageCenter<Data, Report> center) {
        mSortManager.setStorageCenter(center);
    }

    public void setLogFactory(BaseLogFactory<Data, Line, Flag, Log> factory) {
        mSortManager.setLogFactory(factory);
    }

    public void setFilterFactory(BaseFilterFactory<Data, Log> factory) {
        mSortManager.setFilterFactory(factory);
    }

    public void setReporter(BaseReporter<Data, Log, Report> reporter) {
        mSortManager.setReporter(reporter);
    }

    // ****************************************输出API****************************************

    /**
     * 数据源查找，使用指定的数据(全新查)
     */
    public Report find(String dataId, Data data) throws IOException, DataIdException {
        mIndexManager.createIndexes(data);
        return mSortManager.find(dataId, data);
    }

    /**
     * 数据源查找，使用指定的数据id(断点续查)，不改变范围与索引
     */
    public Report find(String dataId) throws IOException, DataIdException {
        return mSortManager.find(dataId);
    }

    /**
     * 报告集中查询
     */
    public Report query(String dataId) throws DataIdException {
        return mSortManager.query(dataId);
    }

    /**
     * 先从报告集中查询，若没有则从数据源查找
     */
    public Report queryAndFind(String dataId) throws IOException, DataIdException {
        return mSortManager.queryAndFind(dataId);
    }
}
