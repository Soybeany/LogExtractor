package com.soybeany.core.index;

import com.soybeany.core.common.BaseIndexCenter;
import com.soybeany.core.common.BaseManager;
import com.soybeany.core.common.Module;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public class IndexManager<Data, Range, Index, RLine, Line, Flag> extends BaseManager<Data, Range, Index, RLine, Line, Flag> {

    public static final String PURPOSE = "索引";

    private BaseCreatorFactory<Data, Index, RLine, Line, Flag> mCreatorFactory;

    // ****************************************设置API****************************************

    public void setCreatorFactory(BaseCreatorFactory<Data, Index, RLine, Line, Flag> factory) {
        mCreatorFactory = factory;
    }

    // ****************************************输出API****************************************

    public void createIndexes(Data data) throws IOException {
        // 检查模块
        checkAndSetupModules(data, Collections.<Module<Data>>singletonList(mCreatorFactory));
        // 加载
        try {
            Index index = openLoader(PURPOSE);
            parseLines(new Callback(index));
        } finally {
            closeLoader();
        }
    }

    // ****************************************重写方法****************************************

    @Override
    protected Index getIndex(BaseIndexCenter<Data, Range, Index> indexCenter) {
        return indexCenter.getSourceIndex();
    }

    // ****************************************内部类****************************************

    private class Callback implements ICallback<RLine, Line, Flag> {
        private Index mIndex;
        private List<? extends BaseIndexCreator<Data, Index, RLine, Line>> mLineCreators = mCreatorFactory.getLineCreators();
        private List<? extends BaseIndexCreator<Data, Index, RLine, Flag>> mFlagCreators = mCreatorFactory.getFlagCreators();

        Callback(Index index) {
            mIndex = index;
        }

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
