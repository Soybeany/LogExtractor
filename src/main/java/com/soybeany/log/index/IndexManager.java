package com.soybeany.log.index;

import com.soybeany.log.base.BaseManager;
import com.soybeany.log.base.BaseIndexCenter;
import com.soybeany.log.base.BaseLoader;
import com.soybeany.log.base.IRawLine;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public class IndexManager<Data, Range, Index, RLine extends IRawLine, Line, Flag> extends BaseManager<Data, RLine, Line, Flag> {

    private BaseIndexCenter<Data, Range, Index> mIndexCenter;
    private BaseLoader<Data, Range, RLine> mLoader;
    private BaseCreatorFactory<Data, Index, RLine, Line, Flag> mCreatorFactory;

    public IndexManager(Data data) {
        super(data);
    }

    // ****************************************设置API****************************************

    public void setCenter(BaseIndexCenter<Data, Range, Index> center) {
        mIndexCenter = center;
    }

    public void setLoader(BaseLoader<Data, Range, RLine> loader) {
        mLoader = loader;
    }

    public void setCreatorFactory(BaseCreatorFactory<Data, Index, RLine, Line, Flag> factory) {
        mCreatorFactory = factory;
    }

    // ****************************************输出API****************************************

    public void createIndexes() throws IOException {
        // 检查模块
        checkAndSetupModules(Arrays.asList(mIndexCenter, mLoader, mCreatorFactory));
        // 加载
        try {
            mLoader.onOpen(mIndexCenter.getLoadRange());
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
        private List<? extends BaseIndexCreator<Data, Index, RLine, Line>> mLineCreators = mCreatorFactory.getLineCreators();
        private List<? extends BaseIndexCreator<Data, Index, RLine, Flag>> mFlagCreators = mCreatorFactory.getFlagCreators();

        public boolean onHandleLineAndFlag(RLine rLine, Line line, Flag flag) {
            // 建立Line索引
            for (BaseIndexCreator<Data, Index, RLine, Line> creator : mLineCreators) {
                creator.onCreateIndex(mIndex, rLine, line);
            }
            // 若为标签，建立标签索引
            if (null != flag) {
                for (BaseIndexCreator<Data, Index, RLine, Flag> creator : mFlagCreators) {
                    creator.onCreateIndex(mIndex, rLine, flag);
                }
            }
            return false;
        }
    }
}
