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

    private BaseDataAccessor<Data, Index, Report> mDataIdAccessor;
    private BaseStorageCenter<Data> mStorageCenter;
    private BaseLogFactory<Data, Line, Flag, Log> mLogFactory;
    private BaseFilterFactory<Data, Log> mFilterFactory;
    private BaseQueryReporter<Data, Log, Report> mReporter;

    // ****************************************设置API****************************************

    public void setDataIdAccessor(BaseDataAccessor<Data, Index, Report> dataIdAccessor) {
        mDataIdAccessor = dataIdAccessor;
    }

    public void setStorageCenter(BaseStorageCenter<Data> center) {
        mStorageCenter = center;
    }

    public void setLogFactory(BaseLogFactory<Data, Line, Flag, Log> factory) {
        mLogFactory = factory;
    }

    public void setFilterFactory(BaseFilterFactory<Data, Log> factory) {
        mFilterFactory = factory;
    }

    public void setReporter(BaseQueryReporter<Data, Log, Report> reporter) {
        mReporter = reporter;
    }

    // ****************************************输出API****************************************

    /**
     * 数据源查找
     */
    public Report find(Data data) throws IOException, DataIdException, ConcurrencyException {
        checkDataStorage();
        checkDataAccessor();
        Report report = innerQuery(data);
        mStorageCenter.saveData(mDataIdAccessor.getCurDataId(data), data);
        return report;
    }

    /**
     * 先从报告集中查询，若没有则从数据源查找
     */
    public Report findById(String dataId) throws IOException, DataIdException, ConcurrencyException {
        checkDataStorage();
        Data data = mStorageCenter.loadData(dataId);
        Report report = mDataIdAccessor.getReport(data);
        if (null == report) {
            checkDataAccessor();
            report = innerQuery(data);
        }
        return report;
    }

    // ****************************************重写方法****************************************

    @Override
    protected Index getIndex(BaseIndexCenter<Data, Range, Index> indexCenter, Data data) {
        Index index = mDataIdAccessor.getIndex(data);
        if (null == index) {
            index = indexCenter.getCopiedIndex();
            mDataIdAccessor.setIndex(data, index);
        }
        return index;
    }

    // ****************************************内部方法****************************************

    private Report innerQuery(Data data) throws IOException, DataIdException, ConcurrencyException {
        // 检查模块
        setAndCheckModules(Arrays.asList(mLogFactory, mFilterFactory, mReporter));
        // 加载
        try {
            init(PURPOSE, data);
            // 按需补充日志
            mLogFactory.attainLock();
            while (mReporter.needMoreLog()) {
                if (parseLines(new Callback())) {
                    break;
                }
            }
            // 生成报告
            Report report = mReporter.getNewReport();
            mDataIdAccessor.setReport(data, report);
            return report;
        } finally {
            mLogFactory.releaseLock();
            finish();
        }
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
