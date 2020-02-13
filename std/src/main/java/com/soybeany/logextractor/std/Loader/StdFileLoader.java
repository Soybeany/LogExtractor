package com.soybeany.logextractor.std.Loader;

import com.soybeany.logextractor.core.query.IQueryListener;
import com.soybeany.logextractor.core.scan.IScanListener;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.sfile.loader.SingleFileLoader;
import com.soybeany.logextractor.std.data.IStdFileLoaderData;
import com.soybeany.logextractor.std.data.IStdFileLoaderParam;

import java.io.IOException;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public class StdFileLoader<Param extends IStdFileLoaderParam, Index, Data extends IStdFileLoaderData> extends SingleFileLoader<Param, Index, Data> implements IScanListener, IQueryListener {

    private Data mData;
    private long mLoadSizeLimit;
    private long mMaxPointer;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);
        mData = data;
        mLoadSizeLimit = param.getLoadSizeLimit();
    }

    @Override
    public void onInit(String purpose, Index index) throws IOException {
        super.onInit(purpose, index);
        mMaxPointer = mData.getCurEndPointer() + mLoadSizeLimit;
    }

    @Override
    public String getNextLine() throws IOException {
        if (mMaxPointer < mData.getCurEndPointer()) {
            mData.setReachLoadLimit(true);
            return null;
        }
        return super.getNextLine();
    }

    @Override
    public void onScanFinish() {
        mData.setScanRange(getLoadRange());
    }

    @Override
    public void onReadyToGenerateReport() {
        mData.setQueryRange(getLoadRange());
    }

    private SFileRange getLoadRange() {
        return SFileRange.between(mData.getStartPointer(), mData.getCurEndPointer());
    }
}
