package com.soybeany.logextractor.sfile;

import com.soybeany.logextractor.core.common.*;
import com.soybeany.logextractor.core.query.*;
import com.soybeany.logextractor.core.query.parser.BaseFlagParser;
import com.soybeany.logextractor.core.query.parser.BaseLineParser;
import com.soybeany.logextractor.core.scan.BaseIndexCreator;
import com.soybeany.logextractor.core.scan.BaseIndexCreatorFactory;
import com.soybeany.logextractor.core.scan.ScanManager;
import com.soybeany.logextractor.core.tool.SimpleUniqueLock;
import com.soybeany.logextractor.sfile.data.*;
import com.soybeany.logextractor.sfile.handler.ISFileIndexHandler;
import com.soybeany.logextractor.sfile.handler.SFileIndexHandlerFactory;
import com.soybeany.logextractor.sfile.loader.SingleFileLoader;
import com.soybeany.logextractor.sfile.merge.RangeMerger;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * 单文件日志提取器
 * <br>自动索引
 * <br>断点续查
 * <br>Created by Soybeany on 2020/2/8.
 */
public class SFileLogExtractor<Param extends ISFileParam, Index extends ISFileIndex, Line, Flag, Log, Report, Data extends SFileData<Param, Index, Report>> {

    private IIdGenerator mIdGenerator = new UUIDGenerator();

    protected ScanManager<Param, Index, Line, Flag, Data> mScanManager;
    protected QueryManager<Param, Index, Line, Flag, Log, Report, Data> mQueryManager;

    private BaseStorageCenter<Index> mIndexStorageCenter;
    private IInstanceFactory<Data> mDataInstanceFactory;
    private BaseStorageCenter<Data> mDataStorageCenter;
    private SFileIndexHandlerFactory<Param, Index, Line, Flag, Data> mIndexHandlerFactory;


    public SFileLogExtractor(IInstanceFactory<Data> dataFactory, IInstanceFactory<Index> indexFactory) {
        ToolUtils.checkNull(dataFactory, "DataInstanceFactory不能设置为null");
        mScanManager = new ScanManager<Param, Index, Line, Flag, Data>(indexFactory);
        mQueryManager = new QueryManager<Param, Index, Line, Flag, Log, Report, Data>(indexFactory);
        mDataInstanceFactory = dataFactory;

        mScanManager.setCreatorFactory(new IndexCreatorFactoryAdapter());
        mQueryManager.addModule(new InnerModule());
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
        mScanManager.setLoader(loader);
        mQueryManager.setLoader(loader);
    }

    public void setLineParser(BaseLineParser<Param, Line, Data> parser) {
        mScanManager.setLineParser(parser);
        mQueryManager.setLineParser(parser);
    }

    public void setFlagParser(BaseFlagParser<Param, Line, Flag, Data> parser) {
        mScanManager.setFlagParser(parser);
        mQueryManager.setFlagParser(parser);
    }

    public void setLogAssembler(BaseLogAssembler<Param, Line, Flag, Log, Data> assembler) {
        mQueryManager.setLogAssembler(assembler);
    }

    public void setFilterFactory(BaseLogFilterFactory<Param, Log, Data> factory) {
        mQueryManager.setFilterFactory(factory);
    }

    public void setReporter(BaseLogReporter<Param, Log, Report, Data> reporter) {
        mQueryManager.setReporter(reporter);
    }

    public void setDataStorageCenter(BaseStorageCenter<Data> center) {
        mDataStorageCenter = center;
    }

    public void setIndexHandlerFactory(SFileIndexHandlerFactory<Param, Index, Line, Flag, Data> factory) {
        mIndexHandlerFactory = factory;
    }

    // ****************************************输出API****************************************

    /**
     * 数据源查找，使用指定的数据(全新查)
     */
    public Report find(Param param) throws InterruptedException {
        ToolUtils.checkNull(param, "param不能为null");
        // 获取数据
        Data data = getData(mIdGenerator.getNewId(), true);
        data.param = param;
        // 更新索引
        mScanManager.createOrUpdateIndexes(param, data);
        // 执行查找并记录报告
        return data.report = mQueryManager.find(param, data);
    }

    /**
     * 先从报告集中查询，若没有则从数据源查找(断点续查)
     */
    public Report findById(String dataId) throws InterruptedException {
        ToolUtils.checkNull(dataId, "DataId不能为null");
        Data data = getData(dataId, false);
        try {
            SimpleUniqueLock.tryAttain(data.getLock(), data.param.getTryLockTimeoutSec());
            if (null == data.report) {
                data.report = mQueryManager.find(data.param, data);
            }
            return data.report;
        } finally {
            SimpleUniqueLock.release(data.getLock());
        }
    }

    // ****************************************内部方法****************************************

    private Data getData(String dataId, boolean needCreate) {
        ToolUtils.checkNull(mDataStorageCenter, "DataStorageCenter不能为null");
        if (!needCreate) {
            return mDataStorageCenter.load(dataId);
        }
        Data data = mDataStorageCenter.loadAndSaveIfNotExist(dataId, mDataInstanceFactory);
        data.setCurDataId(dataId);
        return data;
    }

    private SFileIndexHandlerFactory<Param, Index, Line, Flag, Data> getNonNullIndexHandlerFactory() {
        if (null == mIndexHandlerFactory) {
            mIndexHandlerFactory = new DefaultIndexHandlerFactory();
        }
        return mIndexHandlerFactory;
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

    private interface ICallback<Param extends ISFileParam, Index extends ISFileIndex, Line, Flag, Data, Info> {
        BaseIndexCreator<Index, Info> getNewIndexCreator(ISFileIndexHandler<Param, Index, Line, Flag> handler);
    }

    private class InnerModule extends BaseModule<Param, Data> implements IQueryListener {
        private Data mData;

        @Override
        public void onStart(Param param, Data data) throws Exception {
            super.onStart(param, data);
            mData = data;
            setupLoadRange(param);
        }

        @Override
        public int getCallbackSeq() {
            return SingleFileLoader.CALLBACK_SEQ + 1;
        }

        @Override
        public void onReadyToGenerateReport() {
            long curLineEndPointer = mData.getCurLineRange().end;
            // 文件已加载完
            if (curLineEndPointer == mData.getFileSize()) {
                mData.setNoNextDataReason(IRenewalData.REASON_EOF);
                return;
            }
            SFileRange actMaxLoadRange = mData.getActMaxLoadRange();
            // 范围已加载完
            if (curLineEndPointer == actMaxLoadRange.end) {
                mData.setNoNextDataReason(IRenewalData.REASON_EOR);
                return;
            }
            // 没有加载数据
            if (curLineEndPointer == actMaxLoadRange.start) {
                mData.setNoNextDataReason(IRenewalData.REASON_NOT_LOAD);
                return;
            }
            // 准备下一数据
            Data nextData = getData(mIdGenerator.getNewId(), true);
            nextData.beNextDataOf(mData);
        }

        private void setupLoadRange(Param param) {
            // 若已设置范围，则不再重复设置
            if (null != mData.getUnhandledLoadRanges()) {
                return;
            }
            // 设置范围
            RangeMerger merger = new RangeMerger();
            merger.merge(Collections.singletonList(SFileRange.max()));
            List<? extends ISFileIndexHandler<Param, Index, Line, Flag>> handlers = getNonNullIndexHandlerFactory().getHandlerList();
            if (null != handlers) {
                Index index = mIndexStorageCenter.load(param.getIndexId());
                for (ISFileIndexHandler<Param, Index, Line, Flag> handler : handlers) {
                    merger.merge(handler.getRangeStrict(param, index));
                }
            }
            mData.setUnhandledLoadRanges(merger.getResult().getIntersectionRanges());
        }
    }

    private class IndexCreatorFactoryAdapter extends BaseIndexCreatorFactory<Param, Index, Line, Flag, Data> {
        private SFileRange mCurLineRange;

        @Override
        public void onStart(Param param, Data data) throws Exception {
            super.onStart(param, data);
            mCurLineRange = data.getCurLineRange();
        }

        @Override
        public List<? extends BaseIndexCreator<Index, Line>> getLineIndexCreators() {
            return getCreators(new ICallback<Param, Index, Line, Flag, Data, Line>() {
                @Override
                public BaseIndexCreator<Index, Line> getNewIndexCreator(final ISFileIndexHandler<Param, Index, Line, Flag> handler) {
                    return new BaseIndexCreator<Index, Line>() {
                        @Override
                        public void onCreateIndex(Index index, Line line) {
                            handler.onCreateIndexWithLine(index, line, mCurLineRange);
                        }
                    };
                }
            });
        }

        @Override
        public List<? extends BaseIndexCreator<Index, Flag>> getFlagIndexCreators() {
            return getCreators(new ICallback<Param, Index, Line, Flag, Data, Flag>() {
                @Override
                public BaseIndexCreator<Index, Flag> getNewIndexCreator(final ISFileIndexHandler<Param, Index, Line, Flag> handler) {
                    return new BaseIndexCreator<Index, Flag>() {
                        @Override
                        public void onCreateIndex(Index index, Flag flag) {
                            handler.onCreateIndexWithFlag(index, flag, mCurLineRange);
                        }
                    };
                }
            });
        }

        private <Info> List<BaseIndexCreator<Index, Info>> getCreators(ICallback<Param, Index, Line, Flag, Data, Info> callback) {
            List<? extends ISFileIndexHandler<Param, Index, Line, Flag>> handlers = getNonNullIndexHandlerFactory().getHandlerList();
            if (null == handlers) {
                return null;
            }
            List<BaseIndexCreator<Index, Info>> result = new LinkedList<BaseIndexCreator<Index, Info>>();
            for (final ISFileIndexHandler<Param, Index, Line, Flag> handler : handlers) {
                result.add(callback.getNewIndexCreator(handler));
            }
            return result;
        }
    }

    private class DefaultIndexHandlerFactory extends SFileIndexHandlerFactory<Param, Index, Line, Flag, Data> {
        @Override
        public List<ISFileIndexHandler<Param, Index, Line, Flag>> getHandlerList() {
            return null;
        }
    }
}
