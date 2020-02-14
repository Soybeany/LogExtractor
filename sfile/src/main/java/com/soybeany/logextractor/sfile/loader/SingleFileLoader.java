package com.soybeany.logextractor.sfile.loader;

import com.soybeany.logextractor.core.common.BaseLoader;
import com.soybeany.logextractor.core.query.QueryManager;
import com.soybeany.logextractor.core.scan.IScanListener;
import com.soybeany.logextractor.core.scan.ScanManager;
import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.data.ISFileLoaderData;
import com.soybeany.logextractor.sfile.data.ISFileLoaderParam;
import com.soybeany.logextractor.sfile.data.SFileRange;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class SingleFileLoader<Param extends ISFileLoaderParam, Index extends ISFileIndex, Data extends ISFileLoaderData> extends BaseLoader<Param, Index, Data> implements IScanListener {

    private RandomAccessFile mRaf;
    private File mFile;
    private String mCharset;

    private Data mData;
    private Index mIndex;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);
        mData = data;
        mFile = param.getFileToLoad();
        mCharset = param.getFileCharset();
        mRaf = new BufferedRandomAccessFile(mFile, "r");
    }

    @Override
    public void onInit(String purpose, Index index) throws IOException {
        mIndex = index;
        long fileLength = mFile.length();
        // 获得最大的开始位点
        SFileRange range = getMaxRange(purpose, index);
        long startPointer = mData.getStartPointer();
        mRaf.seek(Math.max(startPointer, range.start));
        // 获得最小的结束位点
        long endPointer = mData.getTargetEndPointer();
        endPointer = Math.min(endPointer, range.end);
        mData.setTargetEndPointer(Math.min(endPointer, fileLength));
        // 更新数据
        startPointer = mRaf.getFilePointer();
        mData.setStartPointer(startPointer);
        mData.setCurEndPointer(startPointer);
        mData.setFileSize(fileLength);
    }

    @Override
    public String getNextLine() throws IOException {
        // 判断是否已到达目标位点
        long pointer = mData.getCurEndPointer();
        if (mData.getTargetEndPointer() <= pointer) {
            return null;
        }
        // todo 移动pointer
        // 读取一行新内容
        String rawLine = mRaf.readLine();
        // 更新当前结束位置
        mData.setCurEndPointer(mRaf.getFilePointer());
        return new String(rawLine.getBytes("ISO-8859-1"), mCharset);
    }

    @Override
    public void onFinish() throws Exception {
        super.onFinish();
        if (null != mRaf) {
            mRaf.close();
        }
        mRaf = null;
        mFile = null;
    }

    @Override
    public void onScanFinish() {
        mIndex.setPointer(mData.getCurEndPointer());
    }

    // ****************************************内部方法****************************************

    private SFileRange getMaxRange(String purpose, Index index) {
        if (ScanManager.PURPOSE.equals(purpose)) {
            return SFileRange.from(index.getPointer());
        }
        SFileRange loadRange;
        if (QueryManager.PURPOSE.equals(purpose) && null != (loadRange = mData.getLoadRange())) {
            return loadRange;
        }
        return SFileRange.max();
    }
}
