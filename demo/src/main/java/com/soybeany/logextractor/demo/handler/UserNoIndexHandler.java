package com.soybeany.logextractor.demo.handler;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.demo.data.Index;
import com.soybeany.logextractor.demo.data.Param;
import com.soybeany.logextractor.demo.data.flag.RequestFlag;
import com.soybeany.logextractor.demo.filter.UserNoFilter;
import com.soybeany.logextractor.demo.util.TypeChecker;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.StdLog;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class UserNoIndexHandler extends BaseKeyIgnoreCaseIndexHandler {
    @Override
    public void onCreateIndexWithLine(Index index, StdLine stdLine, SFileRange lineRange) {
        // 留空
    }

    @Override
    public void onCreateIndexWithFlag(Index index, StdFlag stdFlag, SFileRange flagRange) {
        if (!TypeChecker.isRequest(stdFlag.type)) {
            return;
        }
        addIndexValue(index.userNo, ((RequestFlag) stdFlag).userNo, stdFlag, flagRange);
    }

    @Override
    public List<SFileRange> getRangeStrict(Param param, Index index) {
        return getRange("userNo", index.userNo, param.userNo);
    }

    @Override
    public BaseLogFilter<StdLog> getLogFilter(Param param) {
        if (null == param.userNo) {
            return null;
        }
        return new UserNoFilter(param.userNo);
    }
}
