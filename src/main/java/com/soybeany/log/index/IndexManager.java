package com.soybeany.log.index;

import com.soybeany.log.base.BaseManager;
import com.soybeany.log.base.IIndexCenter;
import com.soybeany.log.base.ISeniorLine;

import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public class IndexManager<PosParam, Position, IndexParam, Index, SLine extends ISeniorLine, Line, Flag> extends BaseManager<SLine, Line, Flag> {

    private IIndexCenter<PosParam, Position, ?, ?, IndexParam, Index> mIndexCenter;
    private IIndexLoader<Position, SLine> mLoader;
    private final List<IIndexCreator<Index, SLine, Line>> mLineCreators = new LinkedList<IIndexCreator<Index, SLine, Line>>();
    private final List<IIndexCreator<Index, SLine, Flag>> mFlagCreators = new LinkedList<IIndexCreator<Index, SLine, Flag>>();

    private final PosParam mPosParam;
    private final IndexParam mIndexParam;

    public IndexManager(PosParam posParam, IndexParam indexParam) {
        mPosParam = posParam;
        mIndexParam = indexParam;
    }

    // ****************************************设置API****************************************

    public void setCenter(IIndexCenter<PosParam, Position, ?, ?, IndexParam, Index> center) {
        mIndexCenter = center;
    }

    public void setLoader(IIndexLoader<Position, SLine> loader) {
        mLoader = loader;
    }

    public void addLineCreator(IIndexCreator<Index, SLine, Line> creator) {
        mLineCreators.add(creator);
    }

    public void addFlagCreator(IIndexCreator<Index, SLine, Flag> creator) {
        mFlagCreators.add(creator);
    }

    // ****************************************输出API****************************************

    public void createIndexes() {
        // 检查模块
        checkModules(mIndexCenter, mLoader);
        // 为加载器设置起点
        mLoader.setOutset(mIndexCenter.getLoadOutset(mPosParam));
        // 创建索引
        parseLines(new Callback());
    }

    protected SLine getNextSLine() {
        return mLoader.getNextSeniorLine();
    }

    // ****************************************内部类****************************************

    private class Callback implements ICallback<SLine, Line, Flag> {
        private Index mIndex = mIndexCenter.getIndex(mIndexParam);

        public boolean onHandleLineAndFlag(SLine sLine, Line line, Flag flag) {
            // 建立Line索引
            for (IIndexCreator<Index, SLine, Line> creator : mLineCreators) {
                creator.onCreateIndex(mIndex, sLine, line);
            }
            // 若为标签，建立标签索引
            if (null != flag) {
                for (IIndexCreator<Index, SLine, Flag> creator : mFlagCreators) {
                    creator.onCreateIndex(mIndex, sLine, flag);
                }
            }
            return false;
        }
    }
}
