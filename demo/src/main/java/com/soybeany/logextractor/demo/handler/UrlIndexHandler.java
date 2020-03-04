package com.soybeany.logextractor.demo.handler;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.demo.data.Index;
import com.soybeany.logextractor.demo.data.Param;
import com.soybeany.logextractor.demo.data.flag.RequestFlag;
import com.soybeany.logextractor.demo.filter.UrlFilter;
import com.soybeany.logextractor.demo.util.TypeChecker;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.StdLog;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class UrlIndexHandler extends BaseKeyIgnoreCaseIndexHandler {

    @Override
    public void onCreateIndexWithLine(Index index, StdLine stdLine, SFileRange lineRange) {
        // 留空
    }

    @Override
    public void onCreateIndexWithFlag(Index index, StdFlag stdFlag, SFileRange flagRange) {
        if (!TypeChecker.isRequest(stdFlag.type)) {
            return;
        }
        addIndexValue(index.url, ((RequestFlag) stdFlag).url, stdFlag, flagRange);
    }

    @Override
    public List<SFileRange> getRangeStrict(Param param, Index index) {
        return getRange("url", index.url, param.url);
    }

    @Override
    public BaseLogFilter<StdLog> getLogFilter(Param param) {
        if (null == param.url) {
            return null;
        }
        return new UrlFilter(param.url);
    }
}
