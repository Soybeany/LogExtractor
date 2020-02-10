package com.soybeany.logextractor.sfile;

import com.soybeany.logextractor.core.common.BaseModule;
import com.soybeany.logextractor.core.common.BaseStorageCenter;
import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.common.ToolUtils;
import com.soybeany.logextractor.core.query.*;
import com.soybeany.logextractor.core.query.parser.BaseFlagParser;
import com.soybeany.logextractor.core.query.parser.BaseLineParser;
import com.soybeany.logextractor.core.scan.BaseCreatorFactory;
import com.soybeany.logextractor.core.scan.ScanManager;
import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.data.SFileData;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.sfile.data.SFileRawLine;
import com.soybeany.logextractor.sfile.loader.SingleFileLoader;

/**
 * 单文件日志提取器
 * <br>自动索引
 * <br>断点续查
 * <br>Created by Soybeany on 2020/2/8.
 */
public class SFileLogExtractor<Index extends ISFileIndex, Line, Flag, Log, Report, Data extends SFileData<Index, Report>> {

    private ScanManager<Index, SFileRawLine, Line, Flag, Data> mScanManager = new ScanManager<Index, SFileRawLine, Line, Flag, Data>();
    private QueryManager<Index, SFileRawLine, Line, Flag, Log, Report, Data> mQueryManager = new QueryManager<Index, SFileRawLine, Line, Flag, Log, Report, Data>();

    private SingleFileLoader<Index, Data> mLoader;
    private BaseStorageCenter<Index, Data> mStorageCenter;
    private BaseQueryReporter<Log, Report, Data> mQueryReporter;
    private IDataCreator<Data> mDataCreator = new IDataCreator<Data>() {
        @Override
        public Data getNewData(Class<Data> clazz) {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                throw new BusinessException("无法创建新的数据实例:" + e.getMessage());
            }
        }
    };

    {
        mQueryManager.addModule(new RenewModule());
    }

    // ****************************************设置API****************************************

    public void setStorageCenter(BaseStorageCenter<Index, Data> center) {
        mStorageCenter = center;
        mScanManager.setStorageCenter(center);
        mQueryManager.setStorageCenter(center);
    }

    public void setLoader(SingleFileLoader<Index, Data> loader) {
        mLoader = loader;
        mLoader.addRangeProvider(new RangeProvider());
        mScanManager.setLoader(loader);
        mQueryManager.setLoader(loader);
    }

    public void setLineParser(BaseLineParser<SFileRawLine, Line, Data> parser) {
        mScanManager.setLineParser(parser);
        mQueryManager.setLineParser(parser);
    }

    public void setFlagParser(BaseFlagParser<Line, Flag, Data> parser) {
        mScanManager.setFlagParser(parser);
        mQueryManager.setFlagParser(parser);
    }

    public void setCreatorFactory(BaseCreatorFactory<Index, SFileRawLine, Line, Flag, Data> factory) {
        mScanManager.setCreatorFactory(factory);
    }

    public void setLogFactory(BaseLogFactory<Line, Flag, Log, Data> factory) {
        mQueryManager.setLogFactory(factory);
    }

    public void setFilterFactory(BaseFilterFactory<Log, Data> factory) {
        mQueryManager.setFilterFactory(factory);
    }

    public void setReporter(BaseQueryReporter<Log, Report, Data> reporter) {
        mQueryReporter = reporter;
        mQueryManager.setReporter(reporter);
    }

    public void setDataCreator(IDataCreator<Data> dataCreator) {
        ToolUtils.checkNull(dataCreator, "DataCreator不能设置为null");
        mDataCreator = dataCreator;
    }

    // ****************************************输出API****************************************

    /**
     * 数据源查找，使用指定的数据(全新查)
     */
    public Report find(Data data) {
        // 更新索引
        mScanManager.createIndexes(data);
        mStorageCenter.getSourceIndex(data).setPointer(mLoader.getLoadedRange().end);
        // 执行查找
        return mQueryManager.find(data);
    }

    /**
     * 先从报告集中查询，若没有则从数据源查找(断点续查)
     */
    public Report findById(String dataId) {
        return mQueryManager.findById(dataId);
    }

    // ****************************************内部类****************************************

    public interface IDataCreator<Data> {
        Data getNewData(Class<Data> clazz);
    }

    private class RangeProvider implements SingleFileLoader.IRangeProvider<Data, Index> {
        @Override
        public SFileRange getLoadRange(String purpose, Index index, Data data) {
            // 断点继续查询
            if (QueryManager.PURPOSE.equals(purpose)) {
                return SFileRange.between(data.getPointer(), index.getPointer());
            }
            // 断点继续索引
            else if (ScanManager.PURPOSE.equals(purpose)) {
                return SFileRange.from(index.getPointer());
            }
            return null;
        }
    }

    private class RenewModule extends BaseModule<Data> implements IQueryListener {
        private Data mData;

        @Override
        public void onStart(Data data) throws Exception {
            super.onStart(data);
            mData = data;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void onReadyToGenerateReport() {
            if (mQueryReporter.needMoreLog() || mLoader.isLoadToEnd()) {
                return;
            }
            Data nextData = mDataCreator.getNewData((Class<Data>) mData.getClass());
            nextData.beNextDataOf(mData);
            nextData.setPointer(mLoader.getLoadedRange().end);
            mStorageCenter.saveData(nextData.getDataId(), nextData);
        }
    }
}
