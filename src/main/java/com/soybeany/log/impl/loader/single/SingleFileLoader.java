package com.soybeany.log.impl.loader.single;

import com.soybeany.log.base.BaseLoader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class SingleFileLoader<Data extends ISFileData> extends BaseLoader<Data, SFileRange, SFileRawLine> {

    private final SFileRawLine mRawLine = new SFileRawLine();
    private RandomAccessFile mRaf;
    private long mLastPointer;

    private File mFile;
    private String mCharset;
    private SFileRange mLoadRange;

    @Override
    public String getNextLineText() throws IOException {
        return innerGetNextRawLine(mRawLine).getLineText();
    }

    @Override
    public SFileRawLine getNextRawLine() throws IOException {
        return innerGetNextRawLine(new SFileRawLine());
    }

    @Override
    public void onOpen(SFileRange range) throws IOException {
        mLastPointer = 0;
        mRaf = new BufferedRandomAccessFile(mFile, "r");

        if (null == range) {
            range = SFileRange.to(mFile.length());
        }
        mLoadRange = range;

        mRaf.seek(mLoadRange.start);
    }

    @Override
    public void onClose() throws IOException {
        mRawLine.update(0, 0, null);
        if (null == mRaf) {
            return;
        }
        mRaf.close();
    }

    @Override
    public void onInit(Data data) {
        mFile = data.getFileToLoad();
        mCharset = data.getFileCharset();
    }

    private SFileRawLine innerGetNextRawLine(SFileRawLine rLine) throws IOException {
        // 判断是否已到达目标位点
        if (mLoadRange.end <= mLastPointer) {
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
