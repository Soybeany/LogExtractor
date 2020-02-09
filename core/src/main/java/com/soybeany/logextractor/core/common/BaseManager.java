package com.soybeany.logextractor.core.common;


import com.soybeany.logextractor.core.query.parser.BaseFlagParser;
import com.soybeany.logextractor.core.query.parser.BaseLineParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 优先使用抽象模块来拓展功能，若模块已是具体实现类，才在数据上使用接口
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseManager<Data, Index, RLine, Line, Flag> {

    private static int WORK_COUNT = 0;

    protected BaseStorageCenter<Data, Index> mStorageCenter;
    private BaseLoader<Data, RLine, Index> mLoader;
    private BaseLineParser<Data, RLine, Line> mLineParser;
    private BaseFlagParser<Data, Line, Flag> mFlagParser;

    private List<BaseModule<Data>> mModules;

    // ****************************************输出API****************************************

    public static int getWorkCount() {
        return WORK_COUNT;
    }

    // ****************************************设置API****************************************

    public void setStorageCenter(BaseStorageCenter<Data, Index> center) {
        mStorageCenter = center;
    }

    public void setLoader(BaseLoader<Data, RLine, Index> loader) {
        mLoader = loader;
    }

    public void setLineParser(BaseLineParser<Data, RLine, Line> parser) {
        mLineParser = parser;
    }

    public void setFlagParser(BaseFlagParser<Data, Line, Flag> parser) {
        mFlagParser = parser;
    }

    // ****************************************子类调用****************************************

    protected void checkDataStorage() {
        ToolUtils.checkNull(mStorageCenter, "StorageCenter未设置");
    }

    protected void setAndCheckModules(List<BaseModule<Data>> modules) {
        mModules = new ArrayList<BaseModule<Data>>(modules);
        // 设置额外检测的模块
        mModules.addAll(Arrays.asList(mLoader, mLineParser, mFlagParser));
        // 检测
        for (int i = 0; i < mModules.size(); i++) {
            BaseModule<Data> module = mModules.get(i);
            ToolUtils.checkNull(module, "模块设置不完整(" + i + ")");
        }
    }

    protected synchronized Index init(String purpose, Data data) throws IOException {
        // 增加计数
        WORK_COUNT++;
        // 触发回调
        for (BaseModule<Data> module : mModules) {
            module.onActivate(data);
        }
        // 开启加载器
        Index index = getIndex(mStorageCenter, data);
        mLoader.onOpen(purpose, index);
        return index;
    }

    protected synchronized void finish() throws IOException {
        // 关闭加载器
        try {
            mLoader.onClose();
        } finally {
            // 触发回调
            for (BaseModule<Data> module : mModules) {
                module.onInactivate();
            }
            // 减少计数
            WORK_COUNT--;
        }
    }

    /**
     * @return 是否到达文本末尾
     */
    protected boolean parseLines(ICallback<RLine, Line, Flag> callback) throws IOException {
        Line lastLine = null;
        RLine rLine;
        String lineString = null;
        // 若数据源有数据，则继续
        while (null != (rLine = mLoader.getNextRawLine()) && null != (lineString = mLineParser.getLineText(rLine))) {
            // 将一行文本转换为行对象
            Line curLine = mLineParser.parse(lineString);
            // 若无法解析，则为上一行对象的文本
            if (null == curLine) {
                // 若有上一行对象，则将此文本添加到该行对象中
                if (null != lastLine) {
                    mLineParser.addContent(lastLine, lineString);
                }
                continue;
            }
            // 执行回调
            boolean needBreak = callback.onHandleLineAndFlag(rLine, curLine, mFlagParser.parse(curLine));
            if (needBreak) {
                break;
            }
            // 更改上一行对象的指向
            lastLine = curLine;
        }
        // 若数据源已没数据，则返回打断标识
        return null == rLine || null == lineString;
    }

    protected abstract Index getIndex(BaseStorageCenter<Data, Index> storageCenter, Data data);

    protected interface ICallback<RLine, Line, Flag> {
        /**
         * @param line 不为null
         * @param flag 此行对应为的标签，可为null
         * @return 是否需要中断
         */
        boolean onHandleLineAndFlag(RLine rLine, Line line, Flag flag);
    }

}
