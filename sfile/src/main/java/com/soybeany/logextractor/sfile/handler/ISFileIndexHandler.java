package com.soybeany.logextractor.sfile.handler;

import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.data.ISFileParam;
import com.soybeany.logextractor.sfile.data.SFileRange;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/14.
 */
public interface ISFileIndexHandler<Param extends ISFileParam, Index extends ISFileIndex, Line, Flag> {

    List<SFileRange> getRangeStrict(Param param, Index index);

    void onCreateIndexWithLine(Index index, Line line);

    void onCreateIndexWithFlag(Index index, Flag flag);
}
