package com.soybeany.logextractor.core.query;

import com.soybeany.logextractor.core.common.BaseManager;
import com.soybeany.logextractor.core.common.BaseModule;
import com.soybeany.logextractor.core.common.IInstanceFactory;
import com.soybeany.logextractor.core.data.BaseData;
import com.soybeany.logextractor.core.data.ICopiableIndex;
import com.soybeany.logextractor.core.data.IIndexIdProvider;

import java.util.Arrays;
import java.util.List;

/**
 * 定义查询流程
 * <br>Created by Soybeany on 2020/2/4.
 */
public class QueryManager<Param extends IIndexIdProvider, Index extends ICopiableIndex, Line, Flag, Log, Report, Data extends BaseData<Index>> extends BaseManager<Param, Index, Line, Flag, Data> {

    public static final String PURPOSE = "整理";

    private IInstanceFactory<Index> mIndexFactory;

    private BaseLogAssembler<Param, Line, Flag, Log, Data> mLogAssembler;
    private BaseFilterFactory<Param, Log, Data> mFilterFactory;
    private BaseLogReporter<Param, Log, Report, Data> mReporter;

    public QueryManager(IInstanceFactory<Index> indexFactory) {
        super(indexFactory);
        mIndexFactory = indexFactory;
    }

    // ****************************************设置API****************************************

    public void setLogAssembler(BaseLogAssembler<Param, Line, Flag, Log, Data> assembler) {
        mLogAssembler = assembler;
    }

    public void setFilterFactory(BaseFilterFactory<Param, Log, Data> factory) {
        mFilterFactory = factory;
    }

    public void setReporter(BaseLogReporter<Param, Log, Report, Data> reporter) {
        mReporter = reporter;
    }

    // ****************************************输出API****************************************

    /**
     * 数据源查找
     */
    public Report find(Param param, Data data) {
        // 检查模块
        setAndCheckModules(Arrays.asList(mLogAssembler, getNonNullFilterFactory(), mReporter));
        // 加载
        try {
            start(PURPOSE, param, data, getIndexFromData(param, data));
            Callback callback = new Callback();
            while (mReporter.needMoreLog()) {
                if (extractLogs(callback)) {
                    // todo 范围到达后，需将未完成的日志也交给后续模块处理
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
            finish();
        }
    }

    // ****************************************内部方法****************************************

    private BaseFilterFactory<Param, Log, Data> getNonNullFilterFactory() {
        if (null == mFilterFactory) {
            mFilterFactory = new DefaultFilterFactory();
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

    /**
     * @return 是否添加成功
     */
    private boolean tryToAddLogToReporter(List<? extends BaseFilter<Param, Log, Data>> filters, Log log) {
        // 过滤日志对象
        if (null != filters) {
            for (BaseFilter<Param, Log, Data> filter : filters) {
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
        private List<? extends BaseFilter<Param, Log, Data>> mFilters = mFilterFactory.getFilters();

        public boolean onHandleLineAndFlag(Line line, Flag flag) {
            Log log;
            // 若不是标签对象，则添加行
            if (null == flag) {
                log = mLogAssembler.addLine(line);
            }
            // 否则添加标签
            else {
                log = mLogAssembler.addFlag(flag);
            }
            // 若没有日志对象则直接返回
            if (null == log) {
                return false;
            }
            // 尝试将日志添加到
            return tryToAddLogToReporter(mFilters, log);
        }
    }

    private class DefaultFilterFactory extends BaseFilterFactory<Param, Log, Data> {
        @Override
        public List<BaseFilter<Param, Log, Data>> getFilters() {
            return null;
        }
    }
}
