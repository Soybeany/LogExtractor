package com.soybeany.logextractor.core.common;


import com.soybeany.logextractor.core.query.parser.BaseFlagParser;
import com.soybeany.logextractor.core.query.parser.BaseLineParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 优先使用抽象模块来拓展功能，若模块已是具体实现类，才在数据上使用接口
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseManager<Param, Index, RLine, Line, Flag, Data> {

    private static int WORK_COUNT = 0;

    private final List<BaseModule<Param, Data>> mExModules = new LinkedList<BaseModule<Param, Data>>();

    protected List<BaseModule<Param, Data>> mModules;

    private IInstanceFactory<Index> mIndexInstanceFactory;
    private BaseStorageCenter<Index> mIndexStorageCenter;

    private BaseLoader<Param, RLine, Index, Data> mLoader;
    private BaseLineParser<Param, RLine, Line, Data> mLineParser;
    private BaseFlagParser<Param, Line, Flag, Data> mFlagParser;

    public BaseManager(IInstanceFactory<Index> factory) {
        ToolUtils.checkNull(factory, "IndexInstanceFactory不能设置为null");
        mIndexInstanceFactory = factory;
    }

    // ****************************************输出API****************************************

    public static int getWorkCount() {
        return WORK_COUNT;
    }

    public void addModule(BaseModule<Param, Data> module) {
        mExModules.add(module);
    }

    // ****************************************设置API****************************************

    public void setIndexStorageCenter(BaseStorageCenter<Index> center) {
        mIndexStorageCenter = center;
    }

    public void setLoader(BaseLoader<Param, RLine, Index, Data> loader) {
        mLoader = loader;
    }

    public void setLineParser(BaseLineParser<Param, RLine, Line, Data> parser) {
        mLineParser = parser;
    }

    public void setFlagParser(BaseFlagParser<Param, Line, Flag, Data> parser) {
        mFlagParser = parser;
    }

    // ****************************************子类调用****************************************

    protected void setAndCheckModules(List<BaseModule<Param, Data>> modules) {
        // 检测非模块组件
        ToolUtils.checkNull(mIndexStorageCenter, "IndexStorageCenter不能设置为null");
        mModules = new ArrayList<BaseModule<Param, Data>>(modules);
        // 设置额外检测的模块
        mModules.addAll(Arrays.asList(mLoader, mLineParser, mFlagParser));
        mModules.addAll(mExModules);
        // 检测模块
        for (int i = 0; i < mModules.size(); i++) {
            BaseModule<Param, Data> module = mModules.get(i);
            ToolUtils.checkNull(module, "模块设置不完整(" + i + ")");
        }
    }

    protected synchronized void start(String purpose, Param param, Data data, Index index) {
        // 触发回调
        invokeOnStart(mModules, param, data);
        // 增加计数
        WORK_COUNT++;
        // 初始化加载器
        try {
            mLoader.onInit(purpose, index);
        } catch (IOException e) {
            throw new BusinessException("加载器初始化异常:" + e.getMessage());
        }
    }

    protected synchronized void finish() {
        // 减少计数
        WORK_COUNT--;
        // 触发回调
        invokeOnFinish(mModules);
    }

    protected Index getIndexFromStorageCenter(String indexId) {
        return mIndexStorageCenter.loadAndSaveIfNotExist(indexId, mIndexInstanceFactory);
    }

    /**
     * @return 是否到达文本末尾
     */
    protected boolean extractLogs(ICallback<RLine, Line, Flag> callback) {
        Line lastLine = null;
        RLine rLine;
        String lineString = null;
        // 若数据源有数据，则继续
        try {
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
        } catch (IOException e) {
            throw new BusinessException("日志提取IO异常:" + e.getMessage());
        }
    }

    // ****************************************内部方法****************************************

    private void invokeOnStart(List<BaseModule<Param, Data>> modules, Param param, Data data) {
        for (BaseModule<Param, Data> module : modules) {
            try {
                module.onStart(param, data);
            } catch (Exception e) {
                throw new BusinessException("模块(" + module + ")Start异常:" + e.getMessage());
            }
        }
    }

    private void invokeOnFinish(List<BaseModule<Param, Data>> modules) {
        String eMsg = null;
        for (BaseModule<Param, Data> module : modules) {
            try {
                module.onFinish();
            } catch (Exception e) {
                eMsg = "模块(" + module + ")Finish异常:" + e.getMessage();
            }
        }
        if (null != eMsg) {
            throw new BusinessException(eMsg);
        }
    }

    // ****************************************内部类****************************************

    protected interface ICallback<RLine, Line, Flag> {
        /**
         * @param line 不为null
         * @param flag 此行对应为的标签，可为null
         * @return 是否需要中断
         */
        boolean onHandleLineAndFlag(RLine rLine, Line line, Flag flag);
    }

}
