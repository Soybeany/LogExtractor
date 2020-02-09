package com.soybeany.logextractor.core.scan;

import com.soybeany.logextractor.core.center.SimpleUniqueLock;
import com.soybeany.logextractor.core.common.BaseManager;
import com.soybeany.logextractor.core.common.BaseModule;
import com.soybeany.logextractor.core.common.BaseStorageCenter;
import com.soybeany.logextractor.core.common.ConcurrencyException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public class ScanManager<Data, Index, RLine, Line, Flag> extends BaseManager<Data, Index, RLine, Line, Flag> {

    public static final String PURPOSE = "索引";

    private BaseCreatorFactory<Data, Index, RLine, Line, Flag> mCreatorFactory;

    // ****************************************设置API****************************************

    public void setCreatorFactory(BaseCreatorFactory<Data, Index, RLine, Line, Flag> factory) {
        mCreatorFactory = factory;
    }

    // ****************************************输出API****************************************

    public void createIndexes(Data data) throws IOException, ConcurrencyException {
        // 检查模块
        checkDataStorage();
        setAndCheckModules(Collections.<BaseModule<Data>>singletonList(mCreatorFactory));
        // 加载
        Index index = null;
        try {
            index = init(PURPOSE, data);
            SimpleUniqueLock.tryAttain(index, "索引正在创建，请稍后");
            parseLines(new Callback(index));
        } finally {
            SimpleUniqueLock.release(index);
            finish();
        }
    }

    // ****************************************重写方法****************************************

    @Override
    protected Index getIndex(BaseStorageCenter<Data, Index> storageCenter, Data data) {
        return storageCenter.getSourceIndex(data);
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
