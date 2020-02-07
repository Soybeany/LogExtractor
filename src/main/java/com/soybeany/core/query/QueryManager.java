package com.soybeany.core.query;

import com.soybeany.core.common.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class QueryManager<Data, Range, Index, RLine, Line, Flag, Log, Report> extends BaseManager<Data, Range, Index, RLine, Line, Flag> {

    public static final String PURPOSE = "整理";

    private BaseDataAccessor<Data> mDataIdAccessor;
    private BaseStorageCenter<Data, Report> mStorageCenter;
    private BaseLogFactory<Data, Line, Flag, Log> mLogFactory;
    private BaseFilterFactory<Data, Log> mFilterFactory;
    private BaseReporter<Data, Log, Report> mReporter;

    // ****************************************设置API****************************************

    public void setDataIdAccessor(BaseDataAccessor<Data> dataIdAccessor) {
        mDataIdAccessor = dataIdAccessor;
    }

    public void setStorageCenter(BaseStorageCenter<Data, Report> center) {
        mStorageCenter = center;
    }

    public void setLogFactory(BaseLogFactory<Data, Line, Flag, Log> factory) {
        mLogFactory = factory;
    }

    public void setFilterFactory(BaseFilterFactory<Data, Log> factory) {
        mFilterFactory = factory;
    }

    public void setReporter(BaseReporter<Data, Log, Report> reporter) {
        mReporter = reporter;
    }

    // ****************************************输出API****************************************

    /**
     * 数据源查找
     */
    public Report find(Data data) throws IOException, DataIdException, ConcurrencyException {
        checkDataStorage();
        checkDataAccessor();
        String dataId = mDataIdAccessor.getCurDataId(data);
        Report report = innerQuery(dataId, data);
        mStorageCenter.saveData(dataId, data);
        return report;
    }

    /**
     * 先从报告集中查询，若没有则从数据源查找
     */
    public Report findById(String dataId) throws IOException, DataIdException, ConcurrencyException {
        checkDataStorage();
        try {
            return mStorageCenter.loadReport(dataId);
        } catch (DataIdException e) {
            checkDataAccessor();
            return innerQuery(dataId, mStorageCenter.loadData(dataId));
        }
    }

    // ****************************************重写方法****************************************

    @Override
    protected Index getIndex(BaseIndexCenter<Data, Range, Index> indexCenter) {
        return indexCenter.getCopiedIndex();
    }

    // ****************************************内部方法****************************************

    private Report innerQuery(String dataId, Data data) throws IOException, DataIdException, ConcurrencyException {
        // 检查模块
        setAndCheckModules(Arrays.asList(mLogFactory, mFilterFactory, mReporter));
        // 加载
        try {
            init(PURPOSE, data);
            // 按需补充日志
            mLogFactory.setLock();
            while (mReporter.needMoreLog()) {
                if (parseLines(new Callback())) {
                    break;
                }
            }
        } finally {
            finish();
        }
        // 按需生成下一数据
        boolean isLoadToEnd = needLoadToEnd();
        if (!mReporter.needMoreLog() && !isLoadToEnd) {
            Data nextData = mDataIdAccessor.getNextData(data);
            String nextDataId = mDataIdAccessor.getCurDataId(nextData);
            mDataIdAccessor.setNextDataId(data, nextDataId);
            mStorageCenter.saveData(nextDataId, nextData);
        }
        // 生成报告
        Report report = mReporter.getNewReport(isLoadToEnd);
        mStorageCenter.saveReport(dataId, report);
        return report;
    }

    private void checkDataStorage() {
        ToolUtils.checkNull(mStorageCenter, "StorageCenter未设置");
    }

    private void checkDataAccessor() {
        ToolUtils.checkNull(mDataIdAccessor, "DataAccessor未设置");
    }

    // ****************************************内部类****************************************

    private class Callback implements ICallback<RLine, Line, Flag> {
        private List<BaseFilter<Data, Log>> mFilters = mFilterFactory.getFilters();

        public boolean onHandleLineAndFlag(RLine rLine, Line line, Flag flag) {
            // 若不是标签对象，则添加行
            if (null == flag) {
                mLogFactory.addLine(line);
                return false;
            }
            // 尝试生成日志对象
            Log log = mLogFactory.addFlag(flag);
            // 若没有日志对象则直接返回
            if (null == log) {
                return false;
            }
            // 过滤日志对象
            for (BaseFilter<Data, Log> filter : mFilters) {
                if (filter.isFiltered(log)) {
                    return false;
                }
            }
            // 有效，则添加到报告中
            mReporter.addLog(log);
            return true;
        }
    }
}
