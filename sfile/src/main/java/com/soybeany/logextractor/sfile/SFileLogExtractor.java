package com.soybeany.logextractor.sfile;

import com.soybeany.logextractor.core.common.*;
import com.soybeany.logextractor.core.query.*;
import com.soybeany.logextractor.core.query.parser.BaseFlagParser;
import com.soybeany.logextractor.core.query.parser.BaseLineParser;
import com.soybeany.logextractor.core.scan.BaseCreatorFactory;
import com.soybeany.logextractor.core.scan.ScanManager;
import com.soybeany.logextractor.sfile.data.*;
import com.soybeany.logextractor.sfile.loader.SingleFileLoader;

import java.util.UUID;

/**
 * 单文件日志提取器
 * <br>自动索引
 * <br>断点续查
 * <br>Created by Soybeany on 2020/2/8.
 */
public class SFileLogExtractor<Param extends ISFileParam, Index extends ISFileIndex, Line, Flag, Log, Report, Data extends SFileData<Param, Index, Report>> {

    private IIdGenerator mIdGenerator = new UUIDGenerator();

    private ScanManager<Param, Index, SFileRawLine, Line, Flag, Data> mScanManager;
    private QueryManager<Param, Index, SFileRawLine, Line, Flag, Log, Report, Data> mQueryManager;

    private IInstanceFactory<Data> mDataInstanceFactory;

    private BaseStorageCenter<Data> mDataStorageCenter;
    private BaseStorageCenter<Index> mIndexStorageCenter;

    private SingleFileLoader<Param, Index, Data> mLoader;
    private BaseQueryReporter<Param, Log, Report, Data> mQueryReporter;

    public SFileLogExtractor(IInstanceFactory<Data> dataFactory, IInstanceFactory<Index> indexFactory) {
        ToolUtils.checkNull(dataFactory, "DataInstanceFactory不能设置为null");
        mScanManager = new ScanManager<Param, Index, SFileRawLine, Line, Flag, Data>(indexFactory);
        mQueryManager = new QueryManager<Param, Index, SFileRawLine, Line, Flag, Log, Report, Data>(indexFactory);
        mDataInstanceFactory = dataFactory;

        mQueryManager.addModule(new RenewModule());
    }

    // ****************************************设置API****************************************

    public void setIdGenerator(IIdGenerator generator) {
        if (null == generator) {
            throw new BusinessException("IdGenerator不能为null");
        }
        mIdGenerator = generator;
    }

    public void setIndexStorageCenter(BaseStorageCenter<Index> center) {
        mScanManager.setIndexStorageCenter(center);
        mQueryManager.setIndexStorageCenter(center);
        mIndexStorageCenter = center;
    }

    public void setLoader(SingleFileLoader<Param, Index, Data> loader) {
        mLoader = loader;
        mLoader.addRangeProvider(new RangeProvider());
        mScanManager.setLoader(loader);
        mQueryManager.setLoader(loader);
    }

    public void setLineParser(BaseLineParser<Param, SFileRawLine, Line, Data> parser) {
        mScanManager.setLineParser(parser);
        mQueryManager.setLineParser(parser);
    }

    public void setFlagParser(BaseFlagParser<Param, Line, Flag, Data> parser) {
        mScanManager.setFlagParser(parser);
        mQueryManager.setFlagParser(parser);
    }

    public void setCreatorFactory(BaseCreatorFactory<Param, Index, SFileRawLine, Line, Flag, Data> factory) {
        mScanManager.setCreatorFactory(factory);
    }

    public void setLogFactory(BaseLogFactory<Param, Line, Flag, Log, Data> factory) {
        mQueryManager.setLogFactory(factory);
    }

    public void setFilterFactory(BaseFilterFactory<Param, Log, Data> factory) {
        mQueryManager.setFilterFactory(factory);
    }

    public void setReporter(BaseQueryReporter<Param, Log, Report, Data> reporter) {
        mQueryManager.setReporter(reporter);
        mQueryReporter = reporter;
    }

    public void setDataStorageCenter(BaseStorageCenter<Data> center) {
        mDataStorageCenter = center;
    }


    // ****************************************输出API****************************************

    /**
     * 数据源查找，使用指定的数据(全新查)
     */
    public Report find(Param param) {
        ToolUtils.checkNull(param, "param不能为null");
        // 获取数据
        Data data = getData(mIdGenerator.getNewId());
        data.param = param;
        // 更新索引
        mScanManager.createIndexes(param, data);
        mIndexStorageCenter.load(param.getIndexId()).setPointer(mLoader.getLoadedRange().end);
        // 执行查找
        Report report = mQueryManager.find(param, data);
        // 记录报告
        data.report = report;
        return report;
    }

    /**
     * 先从报告集中查询，若没有则从数据源查找(断点续查)
     */
    public Report findById(String dataId) {
        ToolUtils.checkNull(dataId, "DataId不能为null");
        Data data = getData(dataId);
        Report report = data.report;
        if (null == report) {
            report = mQueryManager.find(data.param, data);
        }
        return report;
    }

    // ****************************************内部方法****************************************

    private Data getData(String dataId) {
        ToolUtils.checkNull(mDataStorageCenter, "DataStorageCenter不能为null");
        Data data = mDataStorageCenter.loadAndSaveIfNotExist(dataId, mDataInstanceFactory);
        data.setCurDataId(dataId);
        return data;
    }

    // ****************************************静态内部类****************************************

    public interface IIdGenerator {
        String getNewId();
    }

    public static class UUIDGenerator implements IIdGenerator {
        @Override
        public String getNewId() {
            return UUID.randomUUID().toString().replace("-", "");
        }
    }

    public static class SimpleIdGenerator implements IIdGenerator {
        private int a;

        @Override
        public synchronized String getNewId() {
            return ++a + "";
        }
    }

    // ****************************************成员内部类****************************************

    private class RangeProvider implements SingleFileLoader.IRangeProvider<Param, Data, Index> {
        @Override
        public SFileRange getLoadRange(Param param, String purpose, Index index, Data data) {
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

    private class RenewModule extends BaseModule<Param, Data> implements IQueryListener {
        private Data mData;

        @Override
        public void onStart(Param param, Data data) throws Exception {
            super.onStart(param, data);
            mData = data;
        }

        @Override
        public void onReadyToGenerateReport() {
            if (mQueryReporter.needMoreLog() || mLoader.isLoadToEnd()) {
                return;
            }
            Data nextData = getData(mIdGenerator.getNewId());
            nextData.beNextDataOf(mData);
            nextData.setPointer(mLoader.getLoadedRange().end);
        }
    }
}
