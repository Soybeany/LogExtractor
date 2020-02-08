package com.soybeany.sfile.loader;

import com.soybeany.core.common.BaseLoader;
import com.soybeany.sfile.data.ISFileData;
import com.soybeany.sfile.data.SFileRange;
import com.soybeany.sfile.data.SFileRawLine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class SingleFileLoader<Data extends ISFileData> extends BaseLoader<Data, SFileRange, SFileRawLine> {

    private RandomAccessFile mRaf;

    private long mStartPointer;
    private long mTargetPointer;
    private long mLastPointer;

    private File mFile;
    private String mCharset;

    @Override
    public SFileRawLine getNextRawLine() throws IOException {
        return innerGetNextRawLine(new SFileRawLine());
    }

    @Override
    public void onOpen(SFileRange range) throws IOException {
        mLastPointer = 0;
        mRaf = new BufferedRandomAccessFile(mFile, "r");

        mRaf.seek(range.start);
        mStartPointer = mRaf.getFilePointer();
        mTargetPointer = Math.min(mFile.length(), range.end);
    }

    @Override
    public void onClose() throws IOException {
        if (null != mRaf) {
            mRaf.close();
        }
    }

    @Override
    public void onActivate(Data data) {
        super.onActivate(data);
        mFile = data.getFileToLoad();
        mCharset = data.getFileCharset();
    }

    public boolean isLoadToEnd() {
        return mLastPointer == mFile.length();
    }

    public SFileRange getLoadedRange() {
        return SFileRange.between(mStartPointer, mLastPointer);
    }

    // ****************************************内部方法****************************************

    private SFileRawLine innerGetNextRawLine(SFileRawLine rLine) throws IOException {
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
}
