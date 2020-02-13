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
public class ScanManager<Param extends IIndexIdProvider, Index, RLine, Line, Flag, Data> extends BaseManager<Param, Index, RLine, Line, Flag, Data> {

    public static final String PURPOSE = "索引";

    private BaseIndexCreatorFactory<Param, Index, RLine, Line, Flag, Data> mCreatorFactory;

    public ScanManager(IInstanceFactory<Index> indexFactory) {
        super(indexFactory);
    }

    // ****************************************设置API****************************************

    public void setCreatorFactory(BaseIndexCreatorFactory<Param, Index, RLine, Line, Flag, Data> factory) {
        mCreatorFactory = factory;
    }

    // ****************************************输出API****************************************

    public void createIndexes(Param param, Data data) {
        // 检查模块
        setAndCheckModules(Collections.<BaseModule<Param, Data>>singletonList(mCreatorFactory));
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

    // ****************************************内部类****************************************

    private class Callback implements ICallback<RLine, Line, Flag> {
        private Index mIndex;
        private List<? extends BaseIndexCreator<Param, Index, RLine, Line, Data>> mLineCreators = mCreatorFactory.getLineIndexCreators();
        private List<? extends BaseIndexCreator<Param, Index, RLine, Flag, Data>> mFlagCreators = mCreatorFactory.getFlagIndexCreators();

        Callback(Index index) {
            mIndex = index;
        }

        public boolean onHandleLineAndFlag(RLine rLine, Line line, Flag flag) {
            // 建立Line索引
            for (BaseIndexCreator<Param, Index, RLine, Line, Data> creator : mLineCreators) {
                creator.onCreateIndex(mIndex, rLine, line);
            }
            // 若为标签，建立标签索引
            if (null != flag) {
                for (BaseIndexCreator<Param, Index, RLine, Flag, Data> creator : mFlagCreators) {
                    creator.onCreateIndex(mIndex, rLine, flag);
                }
            }
            return false;
        }
    }
}
