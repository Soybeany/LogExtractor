package com.soybeany.log.query;

import com.soybeany.log.base.BaseManager;
import com.soybeany.log.base.IIndexCenter;
import com.soybeany.log.base.ILoader;
import com.soybeany.log.base.IRawLine;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class QueryManager<Param, Range, Line, Flag, Log, Report extends IReport> extends BaseManager<Param, IRawLine, Line, Flag> {

    private IIndexCenter<Param, Range, ?> mIndexCenter;
    private ILoader<Param, Range, ? extends IRawLine> mLoader;
    private ILogFactory<Param, Line, Flag, Log> mLogFactory;
    private IFilterFactory<Param, Log> mFilterFactory;
    private IReporter<Param, Log, Report> mReporter;

    private final IRawLine mRLine = new RawLine();

    public QueryManager(Param param) {
        super(param);
    }

    // ****************************************设置API****************************************

    public void setCenter(IIndexCenter<Param, Range, ?> center) {
        mIndexCenter = center;
    }

    public void setLoader(ILoader<Param, Range, ? extends IRawLine> loader) {
        mLoader = loader;
    }

    public void setLogFactory(ILogFactory<Param, Line, Flag, Log> factory) {
        mLogFactory = factory;
    }

    public void setFilterFactory(IFilterFactory<Param, Log> factory) {
        mFilterFactory = factory;
    }

    public void setReporter(IReporter<Param, Log, Report> reporter) {
        mReporter = reporter;
    }

    // ****************************************输出API****************************************

    public Report query() throws IOException {
        // 检查模块
        checkAndSetupModules(Arrays.asList(mIndexCenter, mLoader, mLogFactory, mFilterFactory, mReporter));
        // 加载
        try {
            mLoader.onOpen();
            mLoader.setRange(mIndexCenter.getLoadRange());
            // 按需补充日志
            while (mReporter.needMoreLog()) {
                if (parseLines(new Callback())) {
                    break;
                }
            }
        } finally {
            mLoader.onClose();
        }
        // 生成报告
        return mReporter.getReport();
    }

    // ****************************************重写方法****************************************

    protected IRawLine getNextRawLine() {
        return mRLine;
    }

    // ****************************************内部类****************************************

    private class RawLine implements IRawLine {
        public String getLineText() throws IOException {
            return mLoader.getNextLineText();
        }
    }

    private class Callback implements ICallback<IRawLine, Line, Flag> {
        private List<IFilter<Param, Log>> mFilters = mFilterFactory.getFilters();

        public boolean onHandleLineAndFlag(IRawLine rLine, Line line, Flag flag) {
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
            for (IFilter<Param, Log> filter : mFilters) {
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
