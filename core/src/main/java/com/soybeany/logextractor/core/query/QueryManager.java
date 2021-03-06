package com.soybeany.logextractor.core.query;

import com.soybeany.logextractor.core.common.BaseManager;
import com.soybeany.logextractor.core.common.BaseModule;
import com.soybeany.logextractor.core.common.IInstanceFactory;
import com.soybeany.logextractor.core.data.BaseData;
import com.soybeany.logextractor.core.data.IBaseParam;
import com.soybeany.logextractor.core.data.ICopiableIndex;
import com.soybeany.logextractor.core.tool.SimpleUniqueLock;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 定义查询流程
 * <br>Created by Soybeany on 2020/2/4.
 */
public class QueryManager<Param extends IBaseParam, Index extends ICopiableIndex, Line, Flag, Log, Report, Data extends BaseData<Index>> extends BaseManager<Param, Index, Line, Flag, Data> {

    public static final String PURPOSE = "整理";

    private IInstanceFactory<Index> mIndexFactory;

    private BaseLogAssembler<Param, Line, Flag, Log, Data> mLogAssembler;
    private BaseLogFilterFactory<Param, Log, Data> mFilterFactory;
    private BaseLogReporter<Param, Log, Report, Data> mReporter;

    public QueryManager(IInstanceFactory<Index> indexFactory) {
        super(indexFactory);
        mIndexFactory = indexFactory;
    }

    // ****************************************设置API****************************************

    public void setLogAssembler(BaseLogAssembler<Param, Line, Flag, Log, Data> assembler) {
        mLogAssembler = assembler;
    }

    public void setFilterFactory(BaseLogFilterFactory<Param, Log, Data> factory) {
        mFilterFactory = factory;
    }

    public void setReporter(BaseLogReporter<Param, Log, Report, Data> reporter) {
        mReporter = reporter;
    }

    // ****************************************输出API****************************************

    /**
     * 数据源查找
     */
    public Report find(Param param, Data data) throws InterruptedException {
        // 检查模块
        setAndCheckModules(Arrays.asList(mLogAssembler, getNonNullFilterFactory(), mReporter));
        // 加载
        try {
            SimpleUniqueLock.tryAttain(data.getLock(), param.getTryLockTimeoutSec());
            start(PURPOSE, param, data, getIndexFromData(param, data));
            Callback callback = new Callback();
            while (mReporter.needMoreLog()) {
                if (extractLogs(callback)) {
                    addIncompleteLogsToReport();
                    break;
                }
            }
            // 执行回调
            for (BaseModule<Param, Data> module : mModules) {
                if (module instanceof IQueryListener) {
                    ((IQueryListener) module).onReadyToGenerateReport();
                }
            }
            // 生成报告
            return mReporter.getNewReport();
        } finally {
            try {
                finish();
            } finally {
                SimpleUniqueLock.release(data.getLock());
            }
        }
    }

    // ****************************************内部方法****************************************

    private BaseLogFilterFactory<Param, Log, Data> getNonNullFilterFactory() {
        if (null == mFilterFactory) {
            mFilterFactory = new DefaultLogFilterFactory();
        }
        return mFilterFactory;
    }

    private Index getIndexFromData(Param param, Data data) {
        Index index = data.index;
        if (null == index) {
            index = mIndexFactory.getNew();
            index.copy(getIndexFromStorageCenter(param.getIndexId()));
            data.index = index;
        }
        return index;
    }

    private void addIncompleteLogsToReport() {
        Collection<Log> logs = mLogAssembler.getIncompleteLogs();
        if (null == logs) {
            return;
        }
        List<? extends BaseLogFilter<Log>> filters = mFilterFactory.getIncompleteLogFilters();
        for (Log log : logs) {
            tryToAddLogToReporter(filters, log, false);
        }
    }

    /**
     * @return 是否添加成功
     */
    private boolean tryToAddLogToReporter(List<? extends BaseLogFilter<Log>> filters, Log log, boolean enableSkipFilter) {
        if (null == log) {
            return false;
        }
        if ((null == filters || filters.isEmpty()) && !enableSkipFilter) {
            return false;
        }
        // 过滤日志对象
        if (null != filters) {
            for (BaseLogFilter<Log> filter : filters) {
                if (filter.isFiltered(log)) {
                    return false;
                }
            }
        }
        // 有效，则添加到报告中
        mReporter.addLog(log);
        return true;
    }

    // ****************************************内部类****************************************

    private class Callback implements ICallback<Line, Flag> {
        private List<? extends BaseLogFilter<Log>> mFilters = mFilterFactory.getLogFilters();
        private List<? extends BaseLogFilter<Log>> mIncompleteFilters = mFilterFactory.getIncompleteLogFilters();

        public boolean onHandleLineAndFlag(Line line, Flag flag) {
            Log log;
            // 若不是标签对象，则添加行
            if (null == flag) {
                log = mLogAssembler.addLine(line);
                return tryToAddLogToReporter(mIncompleteFilters, log, false);
            }
            // 否则添加标签
            log = mLogAssembler.addFlag(flag);
            return tryToAddLogToReporter(mFilters, log, true);
        }
    }

    private class DefaultLogFilterFactory extends BaseLogFilterFactory<Param, Log, Data> {
        @Override
        public List<BaseLogFilter<Log>> getLogFilters() {
            return null;
        }

        @Override
        public List<? extends BaseLogFilter<Log>> getIncompleteLogFilters() {
            return null;
        }
    }
}
