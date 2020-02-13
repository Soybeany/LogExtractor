package com.soybeany.logextractor.sfile.loader;

import com.soybeany.logextractor.core.common.BaseLoader;
import com.soybeany.logextractor.sfile.data.ISFileLoaderData;
import com.soybeany.logextractor.sfile.data.ISFileLoaderParam;
import com.soybeany.logextractor.sfile.data.SFileRange;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class SingleFileLoader<Param extends ISFileLoaderParam, Index, Data extends ISFileLoaderData> extends BaseLoader<Param, Index, Data> {

    private final List<IRangeProvider<Param, Data, Index>> mProviders = new LinkedList<IRangeProvider<Param, Data, Index>>();
    private RandomAccessFile mRaf;

    private Param mParam;
    private Data mData;
    private File mFile;
    private String mCharset;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);

        mParam = param;
        mData = data;

        mFile = param.getFileToLoad();
        mCharset = param.getFileCharset();
        mRaf = new BufferedRandomAccessFile(mFile, "r");
    }

    @Override
    public void onInit(String purpose, Index index) throws IOException {
        long fileLength = mFile.length();
        // 获得最大的开始位点
        SFileRange range = getRange(purpose, index);
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
        // 更新最后位置
        String rawLine = mRaf.readLine();
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

    public void addRangeProvider(IRangeProvider<Param, Data, Index> provider) {
        mProviders.add(provider);
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
