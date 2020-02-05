package com.soybeany.log.query;

import com.soybeany.log.query.parser.IFlagParser;
import com.soybeany.log.query.parser.ILineParser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public class QueryManager<Line, Flag, Log> {

    private ILoader mLoader;
    private ILineParser<Line> mLineParser;
    private IFlagParser<Line, Flag> mFlagParser;
    private ILogFactory<Line, Flag, Log> mLogFactory;
    private final List<IFilter<Log>> mFilters = new LinkedList<IFilter<Log>>();
    private IReporter<Log> mReporter;

    // ****************************************设置API****************************************

    public void setLoader(ILoader loader) {
        mLoader = loader;
    }

    public void setLineParser(ILineParser<Line> parser) {
        mLineParser = parser;
    }

    public void setFlagParser(IFlagParser<Line, Flag> parser) {
        mFlagParser = parser;
    }

    public void setLogFactory(ILogFactory<Line, Flag, Log> factory) {
        mLogFactory = factory;
    }

    public void addFilter(IFilter<Log> filter) {
        mFilters.add(filter);
    }

    public void setReporter(IReporter<Log> reporter) {
        mReporter = reporter;
    }

    // ****************************************输出API****************************************

    public String getResult() {
        // 检查模块
        checkModules();
        // 按需补充日志
        while (mReporter.needMoreLog()) {
            boolean needToBreak = handleLog();
            if (needToBreak) {
                break;
            }
        }
        // 生成报告
        return mReporter.toResult();
    }

    // ****************************************内部方法****************************************

    private void checkModules() {
        for (Object o : Arrays.asList(mLoader, mLineParser, mFlagParser, mLogFactory, mReporter)) {
            if (null == o) {
                throw new RuntimeException("模块设置不完整");
            }
        }
    }

    /**
     * @return 是否需要打断
     */
    private boolean handleLog() {
        Line lastLine = null;
        String lineString;
        // 若数据源有数据，则继续
        while (null != (lineString = mLoader.getNextLine())) {
            // 将一行文本转换为行对象
            Line curLine = mLineParser.parse(lineString);
            // 若无法解析，则为上一行对象的文本
            if (null == curLine) {
                // 若有上一行对象，则将此文本添加到该行对象中
                if (null != lastLine) {
                    mLogFactory.addContent(lastLine, lineString);
                }
                continue;
            }
            // 将行对象转换为日志对象
            Log log = lineToLog(curLine);
            // 若日志对象有效，则返回
            if (null != log) {
                mReporter.addLog(log);
                break;
            }
            // 更改上一行对象的指向
            lastLine = curLine;
        }
        // 若数据源已没数据，则返回打断标识
        return null == lineString;
    }

    private Log lineToLog(Line line) {
        // 尝试将行对象解析为标签对象
        Flag flag = mFlagParser.parse(line);
        // 若不是标签对象，则添加行
        if (null == flag) {
            mLogFactory.addLine(line);
            return null;
        }
        // 尝试生成日志对象
        Log log = mLogFactory.addFlag(flag);
        // 若没有日志对象则直接返回
        if (null == log) {
            return null;
        }
        // 过滤日志对象
        for (IFilter<Log> filter : mFilters) {
            if (filter.isFiltered(log)) {
                return null;
            }
        }
        return log;
    }
}
