package com.soybeany.logextractor.sfile.loader;

import com.soybeany.logextractor.core.common.BaseLoader;
import com.soybeany.logextractor.sfile.data.ISFileLoaderParam;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.sfile.data.SFileRawLine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class SingleFileLoader<Param extends ISFileLoaderParam, Index, Data> extends BaseLoader<Param, SFileRawLine, Index, Data> {

    private final List<IRangeProvider<Param, Data, Index>> mProviders = new LinkedList<IRangeProvider<Param, Data, Index>>();
    private RandomAccessFile mRaf;

    private long mStartPointer;
    private long mTargetPointer;
    private long mLastPointer;

    private Param mParam;
    private Data mData;
    private File mFile;
    private String mCharset;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);
        mLastPointer = 0;

        mParam = param;
        mData = data;

        mFile = param.getFileToLoad();
        mCharset = param.getFileCharset();
        mRaf = new BufferedRandomAccessFile(mFile, "r");
    }

    @Override
    public void onInit(String purpose, Index index) throws IOException {
        SFileRange range = getRange(purpose, index);
        mRaf.seek(range.start);
        mStartPointer = mRaf.getFilePointer();
        mTargetPointer = Math.min(mFile.length(), range.end);
    }

    @Override
    public SFileRawLine getNextRawLine() throws IOException {
        SFileRawLine rLine = new SFileRawLine();
        // 判断是否已到达目标位点
        if (mTargetPointer <= mLastPointer) {
            rLine.update(mLastPointer, mLastPointer, null);
            return rLine;
        }
        // 读取下一行
        String lineText = mRaf.readLine();
        // 获得末尾位置
        long curPointer = mRaf.getFilePointer();
        // 转码
        if (null != lineText) {
            lineText = new String(lineText.getBytes("ISO-8859-1"), mCharset);
        }
        // 更新对象
        rLine.update(mLastPointer, curPointer, lineText);
        // 修改最后位置
        mLastPointer = curPointer;
        return rLine;
    }

    @Override
    public void onFinish() throws Exception {
        super.onFinish();
        if (null != mRaf) {
            mRaf.close();
        }
    }

    public void addRangeProvider(IRangeProvider<Param, Data, Index> provider) {
        mProviders.add(provider);
    }

    public boolean isLoadToEnd() {
        return mLastPointer == mFile.length();
    }

    public SFileRange getLoadedRange() {
        return SFileRange.between(mStartPointer, mLastPointer);
    }

    // ****************************************内部方法****************************************

    private SFileRange getRange(String purpose, Index index) {
        SFileRange range = SFileRange.max();
        for (IRangeProvider<Param, Data, Index> provider : mProviders) {
            SFileRange tmpRange = provider.getLoadRange(mParam, purpose, index, mData);
            if (null == tmpRange) {
                continue;
            }
            if (range.start < tmpRange.start) {
                range.start = tmpRange.start;
            }
            if (range.end > tmpRange.end) {
                range.end = tmpRange.end;
            }
        }
        return range;
    }

    // ****************************************内部类****************************************

    public interface IRangeProvider<Param, Data, Index> {
        SFileRange getLoadRange(Param param, String purpose, Index index, Data data);
    }

}
