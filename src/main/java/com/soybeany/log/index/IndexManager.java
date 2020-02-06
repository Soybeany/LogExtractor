package com.soybeany.log.index;

import com.soybeany.log.base.BaseManager;
import com.soybeany.log.base.IIndexCenter;
import com.soybeany.log.base.ILoader;
import com.soybeany.log.base.IRawLine;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public class IndexManager<RangeParam, Range, IndexParam, Index, RLine extends IRawLine, Line, Flag> extends BaseManager<RLine, Line, Flag> {

    private IIndexCenter<RangeParam, Range, IndexParam, Index> mIndexCenter;
    private ILoader<Range, RLine> mLoader;
    private final List<IIndexCreator<Index, RLine, Line>> mLineCreators = new LinkedList<IIndexCreator<Index, RLine, Line>>();
    private final List<IIndexCreator<Index, RLine, Flag>> mFlagCreators = new LinkedList<IIndexCreator<Index, RLine, Flag>>();

    private final RangeParam mRangeParam;
    private final IndexParam mIndexParam;

    public IndexManager(RangeParam rangeParam, IndexParam indexParam) {
        mRangeParam = rangeParam;
        mIndexParam = indexParam;
    }

    // ****************************************设置API****************************************

    public void setCenter(IIndexCenter<RangeParam, Range, IndexParam, Index> center) {
        mIndexCenter = center;
    }

    public void setLoader(ILoader<Range, RLine> loader) {
        mLoader = loader;
    }

    public void addLineCreator(IIndexCreator<Index, RLine, Line> creator) {
        mLineCreators.add(creator);
    }

    public void addFlagCreator(IIndexCreator<Index, RLine, Flag> creator) {
        mFlagCreators.add(creator);
    }

    // ****************************************输出API****************************************

    public void createIndexes() throws IOException {
        // 检查模块
        checkModules(mIndexCenter, mLoader);
        // 加载
        try {
            mLoader.onOpen();
            mLoader.setRange(mIndexCenter.getLoadRange(mRangeParam));
            // 创建索引
            parseLines(new Callback());
        } finally {
            mLoader.onClose();
        }
    }

    protected RLine getNextRawLine() throws IOException {
        return mLoader.getNextRawLine();
    }

    // ****************************************内部类****************************************

    private class Callback implements ICallback<RLine, Line, Flag> {
        private Index mIndex = mIndexCenter.getIndex(mIndexParam);

        public boolean onHandleLineAndFlag(RLine rLine, Line line, Flag flag) {
            // 建立Line索引
            for (IIndexCreator<Index, RLine, Line> creator : mLineCreators) {
                creator.onCreateIndex(mIndex, rLine, line);
            }
            // 若为标签，建立标签索引
            if (null != flag) {
                for (IIndexCreator<Index, RLine, Flag> creator : mFlagCreators) {
                    creator.onCreateIndex(mIndex, rLine, flag);
                }
            }
            return false;
        }
    }
}
