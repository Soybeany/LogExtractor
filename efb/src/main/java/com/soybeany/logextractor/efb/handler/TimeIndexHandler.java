package com.soybeany.logextractor.efb.handler;

import com.soybeany.logextractor.efb.data.Index;
import com.soybeany.logextractor.efb.data.Param;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class TimeIndexHandler extends BaseIndexHandler {
    @Override
    public List<SFileRange> getRangeStrict(Param param, Index index) {
        int a = 2;
        return null;
    }

    @Override
    public void onCreateIndexWithLine(Index index, StdLine stdLine, SFileRange lineRange) {

    }

    @Override
    public void onCreateIndexWithFlag(Index index, StdFlag stdFlag, SFileRange flagRange) {
        // 留空
    }
}
