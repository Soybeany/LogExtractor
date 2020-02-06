package com.soybeany.log.index;

import com.soybeany.log.base.BaseManager;
import com.soybeany.log.base.IIndexCenter;
import com.soybeany.log.base.ILoader;
import com.soybeany.log.base.IRawLine;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public class IndexManager<Param, Range, Index, RLine extends IRawLine, Line, Flag> extends BaseManager<Param, RLine, Line, Flag> {

    private IIndexCenter<Param, Range, Index> mIndexCenter;
    private ILoader<Param, Range, RLine> mLoader;
    private ICreatorFactory<Param, Index, RLine, Line, Flag> mCreatorFactory;

    public IndexManager(Param param) {
        super(param);
    }

    // ****************************************设置API****************************************

    public void setCenter(IIndexCenter<Param, Range, Index> center) {
        mIndexCenter = center;
    }

    public void setLoader(ILoader<Param, Range, RLine> loader) {
        mLoader = loader;
    }

    public void setCreatorFactory(ICreatorFactory<Param, Index, RLine, Line, Flag> factory) {
        mCreatorFactory = factory;
    }

    // ****************************************输出API****************************************

    public void createIndexes() throws IOException {
        // 检查模块
        checkAndSetupModules(Arrays.asList(mIndexCenter, mLoader, mCreatorFactory));
        // 加载
        try {
            mLoader.onOpen();
            mLoader.setRange(mIndexCenter.getLoadRange());
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
        private Index mIndex = mIndexCenter.getIndex();
        private List<? extends IIndexCreator<Param, Index, RLine, Line>> mLineCreators = mCreatorFactory.getLineCreators();
        private List<? extends IIndexCreator<Param, Index, RLine, Flag>> mFlagCreators = mCreatorFactory.getFlagCreators();

        public boolean onHandleLineAndFlag(RLine rLine, Line line, Flag flag) {
            // 建立Line索引
            for (IIndexCreator<Param, Index, RLine, Line> creator : mLineCreators) {
                creator.onCreateIndex(mIndex, rLine, line);
            }
            // 若为标签，建立标签索引
            if (null != flag) {
                for (IIndexCreator<Param, Index, RLine, Flag> creator : mFlagCreators) {
                    creator.onCreateIndex(mIndex, rLine, flag);
                }
            }
            return false;
        }
    }
}
