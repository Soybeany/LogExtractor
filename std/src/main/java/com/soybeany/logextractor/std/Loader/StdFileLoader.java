package com.soybeany.logextractor.std.Loader;

import com.soybeany.logextractor.core.query.IQueryListener;
import com.soybeany.logextractor.core.scan.IScanListener;
import com.soybeany.logextractor.sfile.data.IFileInfoProvider;
import com.soybeany.logextractor.sfile.loader.SingleFileLoader;
import com.soybeany.logextractor.std.data.ILoadDataAccessor;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public class StdFileLoader<Index, Data extends IFileInfoProvider & ILoadDataAccessor> extends SingleFileLoader<Index, Data> implements IScanListener, IQueryListener {

    private Data mData;

    @Override
    public void onStart(Data data) throws Exception {
        super.onStart(data);
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
