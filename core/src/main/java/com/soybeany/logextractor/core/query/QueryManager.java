package com.soybeany.logextractor.core.query;

import com.soybeany.logextractor.core.common.BaseManager;
import com.soybeany.logextractor.core.common.BaseModule;
import com.soybeany.logextractor.core.data.BaseData;

import java.util.Arrays;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class QueryManager<Index, RLine, Line, Flag, Log, Report, Data extends BaseData<Index, Report>> extends BaseManager<Index, RLine, Line, Flag, Data> {

    public static final String PURPOSE = "整理";

    private BaseLogFactory<Line, Flag, Log, Data> mLogFactory;
    private BaseFilterFactory<Log, Data> mFilterFactory;
    private BaseQueryReporter<Log, Report, Data> mReporter;

    // ****************************************设置API****************************************

    public void setLogFactory(BaseLogFactory<Line, Flag, Log, Data> factory) {
        mLogFactory = factory;
    }

    public void setFilterFactory(BaseFilterFactory<Log, Data> factory) {
        mFilterFactory = factory;
    }

    public void setReporter(BaseQueryReporter<Log, Report, Data> reporter) {
        mReporter = reporter;
    }

    // ****************************************输出API****************************************

    /**
     * 数据源查找
     */
    public Report find(Data data) {
        checkDataStorage();
        Report report = innerQuery(data);
        mStorageCenter.saveData(data.getDataId(), data);
        return report;
    }

    /**
     * 先从报告集中查询，若没有则从数据源查找
     */
    public Report findById(String dataId) {
        checkDataStorage();
        Data data = mStorageCenter.loadData(dataId);
        Report report = data.getReport();
        if (null == report) {
            report = innerQuery(data);
        }
        return report;
    }

    // ****************************************内部方法****************************************

    private Report innerQuery(Data data) {
        // 检查模块
        setAndCheckModules(Arrays.asList(mLogFactory, mFilterFactory, mReporter));
        // 加载
        try {
            start(PURPOSE, data, getIndex(data));
            while (mReporter.needMoreLog()) {
                if (extractLogs(new Callback())) {
                    break;
                }
            }
            // 执行回调
            for (BaseModule<Data> module : mModules) {
                if (module instanceof IQueryListener) {
                    ((IQueryListener) module).onReadyToGenerateReport();
                }
            }
            // 生成报告
            Report report = mReporter.getNewReport();
            data.setReport(report);
            return report;
        } finally {
            finish();
        }
    }

    private Index getIndex(Data data) {
        Index index = data.getIndex();
        if (null == index) {
            index = mStorageCenter.getCopiedIndex(data);
            data.setIndex(index);
        }
        return index;
    }

    // ****************************************内部类****************************************

    private class Callback implements ICallback<RLine, Line, Flag> {
        private List<BaseFilter<Log, Data>> mFilters = mFilterFactory.getFilters();

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
            for (BaseFilter<Log, Data> filter : mFilters) {
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
