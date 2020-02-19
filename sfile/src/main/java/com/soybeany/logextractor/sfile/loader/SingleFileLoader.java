package com.soybeany.logextractor.sfile.loader;

import com.soybeany.logextractor.core.common.BaseLoader;
import com.soybeany.logextractor.core.query.IQueryListener;
import com.soybeany.logextractor.core.scan.IScanListener;
import com.soybeany.logextractor.core.scan.ScanManager;
import com.soybeany.logextractor.sfile.data.ISFileIndex;
import com.soybeany.logextractor.sfile.data.ISFileLoaderData;
import com.soybeany.logextractor.sfile.data.ISFileLoaderParam;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.sfile.merge.RangeMerger;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public class SingleFileLoader<Param extends ISFileLoaderParam, Index extends ISFileIndex, Data extends ISFileLoaderData> extends BaseLoader<Param, Index, Data> implements IScanListener, IQueryListener {

    private RandomAccessFile mRaf;
    private File mFile;
    private long mFileLength;
    private String mCharset;

    private List<SFileRange> mLoadRanges;
    private int mRangeIndex;

    private long mTargetEndPointer;
    private long mRangeEndPointer;

    private Data mData;
    private Index mIndex;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);
        mData = data;
        mFile = param.getFileToLoad();
        mFileLength = mFile.length();
        mCharset = param.getFileCharset();
        mRaf = new BufferedRandomAccessFile(mFile, "r");
        mLoadRanges = new ArrayList<SFileRange>();
        mRangeIndex = 0;
    }

    @Override
    public void onInit(String purpose, Index index) throws IOException {
        mIndex = index;
        setupLoadRanges(purpose, index);
        mRangeEndPointer = mLoadRanges.get(0).end;
        // 获得最大的开始位点
        mRaf.seek(mLoadRanges.get(0).start);
        // 获得最小的结束位点
        mTargetEndPointer = mLoadRanges.get(mLoadRanges.size() - 1).end;
        // 更新数据
        long startPointer = mRaf.getFilePointer();
        SFileRange curLineRange = mData.getCurLineRange();
        SFileRange needLoadRange = mData.getNeedLoadRange();
        curLineRange.updateStart(startPointer);
        curLineRange.updateEnd(startPointer);
        needLoadRange.updateStart(startPointer);
        needLoadRange.updateEnd(mTargetEndPointer);
        mData.setFileSize(mFileLength);
    }

    @Override
    public String getNextLine() throws IOException {
        // 判断是否已到达目标位点
        SFileRange curLineRange = mData.getCurLineRange();
        if (mTargetEndPointer <= curLineRange.end) {
            return null;
        }
        // 移动pointer
        switchPointerIfNeedAndUpdateStartPointer(curLineRange);
        // 读取一行新内容
        String rawLine = mRaf.readLine();
        // 更新当前结束位置
        curLineRange.updateEnd(mRaf.getFilePointer());
        return new String(rawLine.getBytes("ISO-8859-1"), mCharset);
    }

    @Override
    public void onScanFinish() {
        mIndex.setPointer(mData.getCurLineRange().end);
    }

    @Override
    public void onReadyToGenerateReport() {
        int fomIndex = mRangeIndex + 1;
        int toIndex = mLoadRanges.size();
        if (toIndex > fomIndex) {
            mLoadRanges.subList(fomIndex, toIndex).clear();
        }
        mLoadRanges.get(mRangeIndex).updateEnd(mData.getCurLineRange().end);
    }

    @Override
    public void onFinish() throws Exception {
        super.onFinish();
        RandomAccessFile raf = mRaf;
        mRaf = null;
        mFile = null;
        mFileLength = 0;
        mLoadRanges = null;
        if (null != raf) {
            raf.close();
        }
    }

    // ****************************************内部方法****************************************

    private void setupLoadRanges(String purpose, Index index) {
        RangeMerger merger = new RangeMerger();
        // 建立索引
        if (ScanManager.PURPOSE.equals(purpose)) {
            mergeSingleRange(merger, SFileRange.from(index.getPointer()));
        }
        // 其它操作
        else {
            List<SFileRange> ranges;
            // 有限定的查询范围
            if (null != (ranges = mData.getExceptLoadRanges())) {
                merger.merge(ranges);
            }
            // 没有限定的查询范围
            else {
                mergeSingleRange(merger, SFileRange.max());
            }
        }
        // 结合数据中设置的范围
        mergeSingleRange(merger, mData.getNeedLoadRange());
        // 结合文件长度
        mergeSingleRange(merger, SFileRange.to(mFileLength));
        // 得到实际需要加载的范围
        mLoadRanges.addAll(merger.getResult().getIntersectionRanges());
        mData.setActLoadRanges(mLoadRanges);
    }

    private void mergeSingleRange(RangeMerger merger, SFileRange range) {
        merger.merge(Collections.singletonList(range));
    }

    private void switchPointerIfNeedAndUpdateStartPointer(SFileRange curLineRange) throws IOException {
        // 当前范围未读完则继续
        if (mRangeEndPointer > curLineRange.end) {
            curLineRange.updateStart(curLineRange.end);
            return;
        }
        // 切换到下一范围
        SFileRange nextRange = mLoadRanges.get(++mRangeIndex);
        mRaf.seek(nextRange.start);
        mRangeEndPointer = nextRange.end;
        curLineRange.updateStart(mRaf.getFilePointer());
    }
}
