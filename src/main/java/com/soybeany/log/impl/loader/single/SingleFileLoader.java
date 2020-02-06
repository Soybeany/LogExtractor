package com.soybeany.log.impl.loader.single;

import com.soybeany.log.base.ILoader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class SingleFileLoader implements ILoader<SFileRange, SFileRawLine> {

    private final File mFile;
    private final String mCharSet;

    private final SFileRawLine mRawLine = new SFileRawLine();
    private RandomAccessFile mRaf;
    private long mLastPointer;

    public SingleFileLoader(File file) {
        this(file, "utf-8");
    }

    public SingleFileLoader(File file, String charSet) {
        mFile = file;
        mCharSet = charSet;
    }

    public void setRange(SFileRange range) throws IOException {
        if (null == range) {
            return;
        }
        mRaf.seek(range.start);
    }

    public String getNextLineText() throws IOException {
        return innerGetNextRawLine(mRawLine).getLineText();
    }

    public SFileRawLine getNextRawLine() throws IOException {
        return innerGetNextRawLine(new SFileRawLine());
    }

    public void onOpen() throws IOException {
        mLastPointer = 0;
        mRaf = new BufferedRandomAccessFile(mFile, "r");
    }

    public void onClose() throws IOException {
        mRawLine.update(0, 0, null);
        if (null == mRaf) {
            return;
        }
        mRaf.close();
    }

    private SFileRawLine innerGetNextRawLine(SFileRawLine rLine) throws IOException {
        // 读取下一行
        String lineText = mRaf.readLine();
        // 获得末尾位置
        long curPointer = mRaf.getFilePointer();
        // 转码
        if (null != lineText) {
            lineText = new String(lineText.getBytes("ISO-8859-1"), mCharSet);
        }
        // 更新对象
        rLine.update(mLastPointer, curPointer, lineText);
        // 修改最后位置
        mLastPointer = curPointer;
        return rLine;
    }
}
