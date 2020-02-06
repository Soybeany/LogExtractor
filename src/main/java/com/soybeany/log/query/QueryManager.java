package com.soybeany.log.query;

import com.soybeany.log.base.BaseManager;
import com.soybeany.log.base.IIndexCenter;
import com.soybeany.log.base.ILoader;
import com.soybeany.log.base.IRawLine;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class QueryManager<RangeParam, Range, Line, Flag, Log> extends BaseManager<IRawLine, Line, Flag> {

    private IIndexCenter<RangeParam, Range, ?, ?> mIndexCenter;
    private ILoader<Range, ? extends IRawLine> mLoader;
    private ILogFactory<Line, Flag, Log> mLogFactory;
    private final List<IFilter<Log>> mFilters = new LinkedList<IFilter<Log>>();
    private IReporter<Log> mReporter;

    private final IRawLine mRLine = new RawLine();
    private final RangeParam mRangeParam;

    public QueryManager(RangeParam rangeParam) {
        mRangeParam = rangeParam;
    }

    // ****************************************设置API****************************************

    public void setCenter(IIndexCenter<RangeParam, Range, ?, ?> center) {
        mIndexCenter = center;
    }

    public void setLoader(ILoader<Range, ? extends IRawLine> loader) {
        mLoader = loader;
    }

    public void setLogFactory(ILogFactory<Line, Flag, Log> factory) {
        mLogFactory = factory;
    }

    public void setReporter(IReporter<Log> reporter) {
        mReporter = reporter;
    }

    public void addFilter(IFilter<Log> filter) {
        mFilters.add(filter);
    }

    // ****************************************输出API****************************************

    public String getResult() throws IOException {
        // 检查模块
        checkModules(mIndexCenter, mLoader, mLogFactory, mReporter);
        // 加载
        try {
            mLoader.onOpen();
            mLoader.setRange(mIndexCenter.getLoadRange(mRangeParam));
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
        return mReporter.toResult();
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
            for (IFilter<Log> filter : mFilters) {
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
