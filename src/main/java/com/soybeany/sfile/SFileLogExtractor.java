package com.soybeany.sfile;

import com.soybeany.core.common.BaseStorageCenter;
import com.soybeany.core.common.ConcurrencyException;
import com.soybeany.core.common.DataIdException;
import com.soybeany.core.query.BaseFilterFactory;
import com.soybeany.core.query.BaseLogFactory;
import com.soybeany.core.query.BaseQueryReporter;
import com.soybeany.core.query.QueryManager;
import com.soybeany.core.query.parser.BaseFlagParser;
import com.soybeany.core.query.parser.BaseLineParser;
import com.soybeany.core.scan.BaseCreatorFactory;
import com.soybeany.core.scan.ScanManager;
import com.soybeany.sfile.accessor.SFileDataAccessor;
import com.soybeany.sfile.data.ISFileData;
import com.soybeany.sfile.data.ISFileIndex;
import com.soybeany.sfile.data.SFileRange;
import com.soybeany.sfile.data.SFileRawLine;
import com.soybeany.sfile.loader.SingleFileLoader;

import java.io.IOException;

/**
 * 单文件日志提取器
 * <br>自动索引
 * <br>断点续查
 * <br>Created by Soybeany on 2020/2/8.
 */
public class SFileLogExtractor<Data extends ISFileData, Index extends ISFileIndex, Line, Flag, Log, Report> {

    private ScanManager<Data, Index, SFileRawLine, Line, Flag> mScanManager = new ScanManager<Data, Index, SFileRawLine, Line, Flag>();
    private QueryManager<Data, Index, SFileRawLine, Line, Flag, Log, Report> mQueryManager = new QueryManager<Data, Index, SFileRawLine, Line, Flag, Log, Report>();

    private SingleFileLoader<Data, Index> mLoader;

    private SFileDataAccessor<Data, Index, Report> mDataAccessor;
    private BaseStorageCenter<Data, Index> mStorageCenter;
    private BaseQueryReporter<Data, Log, Report> mQueryReporter;

    // ****************************************设置API****************************************

    public void setStorageCenter(BaseStorageCenter<Data, Index> center) {
        mStorageCenter = center;
        mScanManager.setStorageCenter(center);
        mQueryManager.setStorageCenter(center);
    }

    public void setLoader(SingleFileLoader<Data, Index> loader) {
        mLoader = loader;
        mLoader.addRangeProvider(new RangeProvider());
        mScanManager.setLoader(loader);
        mQueryManager.setLoader(loader);
    }

    public void setLineParser(BaseLineParser<Data, SFileRawLine, Line> parser) {
        mScanManager.setLineParser(parser);
        mQueryManager.setLineParser(parser);
    }

    public void setFlagParser(BaseFlagParser<Data, Line, Flag> parser) {
        mScanManager.setFlagParser(parser);
        mQueryManager.setFlagParser(parser);
    }

    public void setCreatorFactory(BaseCreatorFactory<Data, Index, SFileRawLine, Line, Flag> factory) {
        mScanManager.setCreatorFactory(factory);
    }

    public void setDataAccessor(SFileDataAccessor<Data, Index, Report> dataAccessor) {
        mDataAccessor = dataAccessor;
        mQueryManager.setDataIdAccessor(dataAccessor);
    }

    public void setLogFactory(BaseLogFactory<Data, Line, Flag, Log> factory) {
        mQueryManager.setLogFactory(factory);
    }

    public void setFilterFactory(BaseFilterFactory<Data, Log> factory) {
        mQueryManager.setFilterFactory(factory);
    }

    public void setReporter(BaseQueryReporter<Data, Log, Report> reporter) {
        mQueryReporter = reporter;
        mQueryManager.setReporter(reporter);
    }

    // ****************************************输出API****************************************

    /**
     * 数据源查找，使用指定的数据(全新查)
     */
    public Report find(Data data) throws IOException, DataIdException, ConcurrencyException {
        // 更新索引
        mScanManager.createIndexes(data);
        mStorageCenter.getSourceIndex(data).setPointer(mLoader.getLoadedRange().end);
        onCreateIndexesFinish(data);
        // 执行查找
        Report report = mQueryManager.find(data);
        createNextDataIfNeed(data);
        return report;
    }

    /**
     * 先从报告集中查询，若没有则从数据源查找(断点续查)
     */
    public Report findById(String dataId) throws IOException, DataIdException, ConcurrencyException {
        Report report = mQueryManager.findById(dataId);
        createNextDataIfNeed(mStorageCenter.loadData(dataId));
        return report;
    }

    // ****************************************子类回调****************************************

    protected void onCreateIndexesFinish(Data data) {
        // 子类按需实现
    }

    // ****************************************内部方法****************************************

    private void createNextDataIfNeed(Data data) throws DataIdException {
        if (mQueryReporter.needMoreLog() || mLoader.isLoadToEnd()) {
            return;
        }
        Data nextData = mDataAccessor.getNewData(data);
        String curDataId = mDataAccessor.getCurDataId(data);
        String nextDataId = mDataAccessor.getCurDataId(nextData);
        mDataAccessor.setLastDataId(nextData, curDataId);
        mDataAccessor.setNextDataId(data, nextDataId);
        mDataAccessor.setPointer(nextData, mLoader.getLoadedRange().end);
        mStorageCenter.saveData(nextDataId, nextData);
    }

    // ****************************************内部类****************************************

    private class RangeProvider implements SingleFileLoader.IRangeProvider<Data, Index> {
        @Override
        public SFileRange getLoadRange(String purpose, Index index, Data data) {
            // 断点继续查询
            if (QueryManager.PURPOSE.equals(purpose)) {
                return SFileRange.between(mDataAccessor.getPointer(data), index.getPointer());
            }
            // 断点继续索引
            else if (ScanManager.PURPOSE.equals(purpose)) {
                return SFileRange.from(index.getPointer());
            }
            return null;
        }
    }
}
