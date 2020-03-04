package com.soybeany.logextractor.sfile.handler;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.data.ISFileParam;
import com.soybeany.logextractor.sfile.data.SFileRange;

import java.util.List;

/**
 * 索引处理器
 * <br>Created by Soybeany on 2020/2/14.
 */
public abstract class SFileIndexHandler<Param extends ISFileParam, Index extends ISFileIndex, Line, Flag, Log> {

    /**
     * 基于Line创建索引
     */
    public abstract void onCreateIndexWithLine(Index index, Line line, SFileRange lineRange);

    /**
     * 基于Flag创建索引
     */
    public abstract void onCreateIndexWithFlag(Index index, Flag flag, SFileRange flagRange);

    /**
     * 根据索引限制加载的范围，可选，与param有关
     */
    public abstract List<SFileRange> getRangeStrict(Param param, Index index);

    /**
     * 在加载的范围中使用过滤器作精确筛选，可选，与param有关
     */
    public abstract BaseLogFilter<Log> getLogFilter(Param param);
}
