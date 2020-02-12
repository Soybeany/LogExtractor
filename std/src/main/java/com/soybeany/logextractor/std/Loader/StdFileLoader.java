package com.soybeany.logextractor.std.Loader;

import com.soybeany.logextractor.core.query.IQueryListener;
import com.soybeany.logextractor.core.scan.IScanListener;
import com.soybeany.logextractor.sfile.data.ISFileLoaderParam;
import com.soybeany.logextractor.sfile.loader.SingleFileLoader;
import com.soybeany.logextractor.std.data.ILoadDataAccessor;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public class StdFileLoader<Param extends ISFileLoaderParam, Index, Data extends ILoadDataAccessor> extends SingleFileLoader<Param, Index, Data> implements IScanListener, IQueryListener {

    private Data mData;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);
        mData = data;
    }

    @Override
    public void onScanFinish() {
        mData.setScanRange(getLoadedRange());
    }

    @Override
    public void onReadyToGenerateReport() {
        mData.setQueryRange(getLoadedRange());
        mData.setCanQueryMore(!isLoadToEnd());
    }
}
