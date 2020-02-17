package com.soybeany.logextractor.std.Loader;

import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.sfile.loader.SingleFileLoader;
import com.soybeany.logextractor.std.data.IStdFileLoaderData;
import com.soybeany.logextractor.std.data.IStdFileLoaderParam;

import java.io.IOException;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/9.
 */
public class StdFileLoader<Param extends IStdFileLoaderParam, Index extends ISFileIndex, Data extends IStdFileLoaderData> extends SingleFileLoader<Param, Index, Data> {

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
        super.onScanFinish();
        mData.setScanRange(SFileRange.between(mData.getStartPointer(), mData.getCurEndPointer()));
    }

    @Override
    public void onReadyToGenerateReport() {
        super.onReadyToGenerateReport();
        List<SFileRange> loadRanges = mData.getActLoadRanges();
        long loadedPointer = 0;
        for (SFileRange range : loadRanges) {
            loadedPointer += range.end - range.start;
        }
        mData.setQueryLoad(loadedPointer);
    }

}
