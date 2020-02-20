package com.soybeany.logextractor.efb.handler;

import com.soybeany.logextractor.efb.data.Index;
import com.soybeany.logextractor.efb.data.Param;
import com.soybeany.logextractor.efb.data.flag.RequestFlag;
import com.soybeany.logextractor.efb.util.TypeChecker;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class UrlIndexHandler extends BaseKeyIgnoreCaseIndexHandler {

    @Override
    public List<SFileRange> getRangeStrict(Param param, Index index) {
        return getRange("url", index.url, param.url);
    }

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
}