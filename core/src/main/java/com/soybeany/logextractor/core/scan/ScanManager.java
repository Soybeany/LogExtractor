package com.soybeany.logextractor.core.scan;

import com.soybeany.logextractor.core.center.SimpleUniqueLock;
import com.soybeany.logextractor.core.common.BaseManager;
import com.soybeany.logextractor.core.common.BaseModule;
import com.soybeany.logextractor.core.common.IInstanceFactory;
import com.soybeany.logextractor.core.data.IIndexIdProvider;
import com.soybeany.logextractor.core.query.IQueryListener;

import java.util.Collections;
import java.util.List;

/**
 * 定义扫描流程
 * <br>Created by Soybeany on 2020/2/5.
 */
public class ScanManager<Param extends IIndexIdProvider, Index, Line, Flag, Data> extends BaseManager<Param, Index, Line, Flag, Data> {

    public static final String PURPOSE = "索引";

    private BaseIndexCreatorFactory<Param, Index, Line, Flag, Data> mCreatorFactory;

    public ScanManager(IInstanceFactory<Index> indexFactory) {
        super(indexFactory);
    }

    // ****************************************设置API****************************************

    public void setCreatorFactory(BaseIndexCreatorFactory<Param, Index, Line, Flag, Data> factory) {
        mCreatorFactory = factory;
    }

    // ****************************************输出API****************************************

    public void createIndexes(Param param, Data data) {
        // 检查模块
        setAndCheckModules(Collections.<BaseModule<Param, Data>>singletonList(getNonNullIndexCreatorFactory()));
        // 加载
        Index index = null;
        String lockId = hashCode() + "";
        try {
            index = getIndexFromStorageCenter(param.getIndexId());
            start(PURPOSE, param, data, index);
            SimpleUniqueLock.tryAttain(lockId, index, "索引正在创建，请稍后");
            extractLogs(new Callback(index));
            // 执行回调
            for (BaseModule<Param, Data> module : mModules) {
                if (module instanceof IQueryListener) {
                    ((IScanListener) module).onScanFinish();
                }
            }
        } finally {
            SimpleUniqueLock.release(lockId, index);
            finish();
        }
    }

    // ****************************************内部方法****************************************

    private BaseIndexCreatorFactory<Param, Index, Line, Flag, Data> getNonNullIndexCreatorFactory() {
        if (null == mCreatorFactory) {
            mCreatorFactory = new DefaultIndexCreatorFactory();
        }
        return mCreatorFactory;
    }

    // ****************************************内部类****************************************

    private class Callback implements ICallback<Line, Flag> {
        private Index mIndex;
        private List<? extends BaseIndexCreator<Param, Index, Line, Data>> mLineCreators = mCreatorFactory.getLineIndexCreators();
        private List<? extends BaseIndexCreator<Param, Index, Flag, Data>> mFlagCreators = mCreatorFactory.getFlagIndexCreators();

        Callback(Index index) {
            mIndex = index;
        }

        public boolean onHandleLineAndFlag(Line line, Flag flag) {
            // 建立Line索引
            if (null != mLineCreators) {
                for (BaseIndexCreator<Param, Index, Line, Data> creator : mLineCreators) {
                    creator.onCreateIndex(mIndex, line);
                }
            }
            // 若为标签，建立标签索引
            if (null != flag && null != mFlagCreators) {
                for (BaseIndexCreator<Param, Index, Flag, Data> creator : mFlagCreators) {
                    creator.onCreateIndex(mIndex, flag);
                }
            }
            return false;
        }
    }

    private class DefaultIndexCreatorFactory extends BaseIndexCreatorFactory<Param, Index, Line, Flag, Data> {
        @Override
        public List<? extends BaseIndexCreator<Param, Index, Line, Data>> getLineIndexCreators() {
            return null;
        }

        @Override
        public List<? extends BaseIndexCreator<Param, Index, Flag, Data>> getFlagIndexCreators() {
            return null;
        }
    }
}
