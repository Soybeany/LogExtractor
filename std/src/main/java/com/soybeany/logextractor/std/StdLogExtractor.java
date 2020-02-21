package com.soybeany.logextractor.std;

import com.soybeany.logextractor.core.common.*;
import com.soybeany.logextractor.core.query.IQueryListener;
import com.soybeany.logextractor.core.scan.IScanListener;
import com.soybeany.logextractor.sfile.SFileLogExtractor;
import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.loader.SingleFileLoader;
import com.soybeany.logextractor.std.Loader.StdFileLoader;
import com.soybeany.logextractor.std.data.IStdParam;
import com.soybeany.logextractor.std.data.StdData;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.StdLog;
import com.soybeany.logextractor.std.data.flag.StdFlag;
import com.soybeany.logextractor.std.reporter.StdLogReporter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 标准日志提取器
 * <br>超时自动删除Index、Data缓存 // todo 待完成
 * <br>提供标准的报告内容
 * <br>Created by Soybeany on 2020/2/8.
 */
public class StdLogExtractor<Param extends IStdParam, Index extends ISFileIndex, Report, Data extends StdData<Param, Index, Report>> extends SFileLogExtractor<Param, Index, StdLine, StdFlag, StdLog, Report, Data> {

    private static final ScheduledExecutorService SERVICE = Executors.newSingleThreadScheduledExecutor();

    private IInstanceFactory<Index> mIndexFactory;
    private BaseStorageCenter<Index> mIndexStorageCenter;

    private final TimingModule mTimingModule = new TimingModule();

    public StdLogExtractor(Class<Data> dataClass, Class<Index> indexClass) {
        this(new DefaultInstanceFactory<Data>(dataClass), new DefaultInstanceFactory<Index>(indexClass));
    }

    public StdLogExtractor(IInstanceFactory<Data> dataFactory, IInstanceFactory<Index> indexFactory) {
        super(dataFactory, indexFactory);
        mIndexFactory = indexFactory;

        mScanManager.addModule(mTimingModule);
        mQueryManager.addModule(mTimingModule);
    }

    // ****************************************设置方法****************************************

    @Override
    public void setIndexStorageCenter(BaseStorageCenter<Index> center) {
//        if (!(center instanceof IStdStorageCenter)) {
//            throw new BusinessException("center请实现IStdStorageCenter接口");
//        }
        super.setIndexStorageCenter(center);
        mIndexStorageCenter = center;
    }

    @Override
    public void setDataStorageCenter(BaseStorageCenter<Data> center) {
//        if (!(center instanceof IStdStorageCenter)) {
//            throw new BusinessException("center请实现IStdStorageCenter接口");
//        }
        super.setDataStorageCenter(center);
    }

    @Override
    public void setLoader(SingleFileLoader<Param, Index, Data> loader) {
        if (!(loader instanceof StdFileLoader)) {
            throw new BusinessException("请使用StdFileLoader作为入参");
        }
        super.setLoader(loader);
    }

    // ****************************************输出API****************************************

    public Index getIndex(String indexId) {
        ToolUtils.checkNull(mIndexStorageCenter, "IndexStorageCenter未设置");
        Index newIndex = mIndexFactory.getNew();
        newIndex.copy(mIndexStorageCenter.load(indexId));
        return newIndex;
    }

    // ****************************************子类重写****************************************

    @Override
    public Report find(Param param) throws InterruptedException {
        param.onCheckParams();
        mTimingModule.startRecord();
        return super.find(param);
    }

    @Override
    public Report findById(String dataId) throws InterruptedException {
        mTimingModule.startRecord();
        return super.findById(dataId);
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

    private class TimingModule extends BaseModule<Param, Data> implements IScanListener, IQueryListener {

        private long mStartTime;
        private Data mData;

        @Override
        public int getCallbackSeq() {
            return StdLogReporter.CALLBACK_SEQ - 1;
        }

        @Override
        public void onStart(Param param, Data data) throws Exception {
            super.onStart(param, data);
            mData = data;
        }

        @Override
        public void onScanFinish() {
            mData.setScanSpend(getSpend());
        }

        @Override
        public void onReadyToGenerateReport() {
            mData.setQuerySpend(getSpend());
        }

        public void startRecord() {
            mStartTime = System.currentTimeMillis();
        }

        private long getSpend() {
            return System.currentTimeMillis() - mStartTime;
        }
    }

}
