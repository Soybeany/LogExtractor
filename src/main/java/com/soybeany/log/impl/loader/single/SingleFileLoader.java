package com.soybeany.log.impl.loader.single;

import com.soybeany.log.base.ILoader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class SingleFileLoader<Param extends ISFileParam> implements ILoader<Param, SFileRange, SFileRawLine> {

    private final SFileRawLine mRawLine = new SFileRawLine();
    private RandomAccessFile mRaf;
    private long mLastPointer;

    private File mFile;
    private String mCharSet;
    private long mGoalPointer;

    public void setRange(SFileRange range) throws IOException {
        if (null == range) {
            return;
        }
        mRaf.seek(range.start);
        mGoalPointer = range.end;
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

    public void onInit(Param param) {
        mFile = param.getFileToLoad();
        mCharSet = param.getFileCharSet();
        mGoalPointer = mFile.length();
    }

    private SFileRawLine innerGetNextRawLine(SFileRawLine rLine) throws IOException {
        // 判断是否已到达目标位点
        if (mGoalPointer <= mLastPointer) {
            rLine.update(mLastPointer, mLastPointer, null);
            return rLine;
        }
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
