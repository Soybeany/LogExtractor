package com.soybeany.log.query;

import com.soybeany.log.base.BaseManager;
import com.soybeany.log.base.IIndexCenter;
import com.soybeany.log.base.ISeniorLine;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class QueryManager<RangeParam, Range, Line, Flag, Log> extends BaseManager<ISeniorLine, Line, Flag> {

    private IIndexCenter<?, ?, RangeParam, Range, ?, ?> mIndexCenter;
    private ILoader<Range> mLoader;
    private ILogFactory<Line, Flag, Log> mLogFactory;
    private final List<IFilter<Log>> mFilters = new LinkedList<IFilter<Log>>();
    private IReporter<Log> mReporter;

    private final ISeniorLine mSLine = new SeniorLine();
    private final RangeParam mRangeParam;

    public QueryManager(RangeParam param) {
        mRangeParam = param;
    }

    // ****************************************设置API****************************************

    public void setCenter(IIndexCenter<?, ?, RangeParam, Range, ?, ?> center) {
        mIndexCenter = center;
    }

    public void setLoader(ILoader<Range> loader) {
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

    public String getResult() {
        // 检查模块
        checkModules(mIndexCenter, mLoader, mLogFactory, mReporter);
        // 为加载器设置范围
        mLoader.setRange(mIndexCenter.getLoadRange(mRangeParam));
        // 按需补充日志
        while (mReporter.needMoreLog()) {
            if (parseLines(new Callback())) {
                break;
            }
        }
        // 生成报告
        return mReporter.toResult();
    }

    // ****************************************重写方法****************************************

    protected ISeniorLine getNextSLine() {
        return mSLine;
    }

    // ****************************************内部类****************************************

    private class SeniorLine implements ISeniorLine {
        public String getLineString() {
            return mLoader.getNextLine();
        }
    }

    private class Callback implements ICallback<ISeniorLine, Line, Flag> {
        public boolean onHandleLineAndFlag(ISeniorLine sLine, Line line, Flag flag) {
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
