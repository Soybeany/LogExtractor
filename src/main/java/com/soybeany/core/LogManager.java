package com.soybeany.core;

import com.soybeany.core.common.*;
import com.soybeany.core.query.*;
import com.soybeany.core.query.parser.BaseFlagParser;
import com.soybeany.core.query.parser.BaseLineParser;
import com.soybeany.core.scan.BaseCreatorFactory;
import com.soybeany.core.scan.ScanManager;

import java.io.IOException;

/**
 * 日志管理器(Facade)
 * <br>Created by Soybeany on 2020/2/7.
 */
public class LogManager<Data, Range, Index, RLine, Line, Flag, Log, Report> {

    private ScanManager<Data, Range, Index, RLine, Line, Flag> mScanManager = new ScanManager<Data, Range, Index, RLine, Line, Flag>();
    private QueryManager<Data, Range, Index, RLine, Line, Flag, Log, Report> mQueryManager = new QueryManager<Data, Range, Index, RLine, Line, Flag, Log, Report>();

    // ****************************************设置API****************************************

    public void setIndexCenter(BaseIndexCenter<Data, Range, Index> center) {
        mScanManager.setIndexCenter(center);
        mQueryManager.setIndexCenter(center);
    }

    public void setLoader(BaseLoader<Data, Range, RLine> loader) {
        mScanManager.setLoader(loader);
        mQueryManager.setLoader(loader);
    }

    public void setLineParser(BaseLineParser<Data, RLine, Line> parser) {
        mScanManager.setLineParser(parser);
        mQueryManager.setLineParser(parser);
    }

    public void setFlagParser(BaseFlagParser<Data, Line, Flag> parser) {
        mScanManager.setFlagParser(parser);
        mQueryManager.setFlagParser(parser);
    }

    public void setCreatorFactory(BaseCreatorFactory<Data, Index, RLine, Line, Flag> factory) {
        mScanManager.setCreatorFactory(factory);
    }

    public void setDataIdAccessor(BaseDataAccessor<Data> dataIdAccessor) {
        mQueryManager.setDataIdAccessor(dataIdAccessor);
    }

    public void setStorageCenter(BaseStorageCenter<Data, Report> center) {
        mQueryManager.setStorageCenter(center);
    }

    public void setLogFactory(BaseLogFactory<Data, Line, Flag, Log> factory) {
        mQueryManager.setLogFactory(factory);
    }

    public void setFilterFactory(BaseFilterFactory<Data, Log> factory) {
        mQueryManager.setFilterFactory(factory);
    }

    public void setReporter(BaseReporter<Data, Log, Report> reporter) {
        mQueryManager.setReporter(reporter);
    }

    // ****************************************输出API****************************************

    /**
     * 创建索引
     */
    public void createIndexes(Data data) throws IOException, ConcurrencyException {
        mScanManager.createIndexes(data);
    }

    /**
     * 数据源查找，使用指定的数据(全新查)
     */
    public Report find(Data data) throws IOException, DataIdException, ConcurrencyException {
        return mQueryManager.find(data);
    }

    /**
     * 先从报告集中查询，若没有则从数据源查找(断点续查)
     */
    public Report findById(String dataId) throws IOException, DataIdException, ConcurrencyException {
        return mQueryManager.findById(dataId);
    }
}
