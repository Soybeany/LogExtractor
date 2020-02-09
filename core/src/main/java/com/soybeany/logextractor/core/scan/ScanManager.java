package com.soybeany.logextractor.core.scan;

import com.soybeany.logextractor.core.center.SimpleUniqueLock;
import com.soybeany.logextractor.core.common.BaseManager;
import com.soybeany.logextractor.core.common.BaseModule;
import com.soybeany.logextractor.core.query.IQueryListener;

import java.util.Collections;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public class ScanManager<Index, RLine, Line, Flag, Data> extends BaseManager<Index, RLine, Line, Flag, Data> {

    public static final String PURPOSE = "索引";

    private BaseCreatorFactory<Index, RLine, Line, Flag, Data> mCreatorFactory;

    // ****************************************设置API****************************************

    public void setCreatorFactory(BaseCreatorFactory<Index, RLine, Line, Flag, Data> factory) {
        mCreatorFactory = factory;
    }

    // ****************************************输出API****************************************

    public void createIndexes(Data data) {
        // 检查模块
        checkDataStorage();
        setAndCheckModules(Collections.<BaseModule<Data>>singletonList(mCreatorFactory));
        // 加载
        Index index = null;
        String lockId = hashCode() + "";
        try {
            index = mStorageCenter.getSourceIndex(data);
            start(PURPOSE, data, index);
            SimpleUniqueLock.tryAttain(lockId, index, "索引正在创建，请稍后");
            extractLogs(new Callback(index));
            // 执行回调
            for (BaseModule<Data> module : mModules) {
                if (module instanceof IQueryListener) {
                    ((IScanListener) module).onScanFinish();
                }
            }
        } finally {
            SimpleUniqueLock.release(lockId, index);
            finish();
        }
    }

    // ****************************************内部类****************************************

    private class Callback implements ICallback<RLine, Line, Flag> {
        private Index mIndex;
        private List<? extends BaseIndexCreator<Index, RLine, Line, Data>> mLineCreators = mCreatorFactory.getLineCreators();
        private List<? extends BaseIndexCreator<Index, RLine, Flag, Data>> mFlagCreators = mCreatorFactory.getFlagCreators();

        Callback(Index index) {
            mIndex = index;
        }

        public boolean onHandleLineAndFlag(RLine rLine, Line line, Flag flag) {
            // 建立Line索引
            for (BaseIndexCreator<Index, RLine, Line, Data> creator : mLineCreators) {
                creator.onCreateIndex(mIndex, rLine, line);
            }
            // 若为标签，建立标签索引
            if (null != flag) {
                for (BaseIndexCreator<Index, RLine, Flag, Data> creator : mFlagCreators) {
                    creator.onCreateIndex(mIndex, rLine, flag);
                }
            }
            return false;
        }
    }
}
