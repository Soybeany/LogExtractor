package com.soybeany.log.base;


import com.soybeany.log.query.parser.IFlagParser;
import com.soybeany.log.query.parser.ILineParser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public abstract class BaseManager<SLine extends ISeniorLine, Line, Flag> {

    private ILineParser<Line> mLineParser;
    private IFlagParser<Line, Flag> mFlagParser;

    // ****************************************设置API****************************************

    public void setLineParser(ILineParser<Line> parser) {
        mLineParser = parser;
    }

    public void setFlagParser(IFlagParser<Line, Flag> parser) {
        mFlagParser = parser;
    }

    // ****************************************子类调用****************************************

    protected void checkModules(Object... modules) {
        List<Object> moduleList = new LinkedList<Object>(Arrays.asList(modules));
        // 设置额外检测的模块
        moduleList.add(mLineParser);
        moduleList.add(mFlagParser);
        // 开始检测
        for (int i = 0; i < moduleList.size(); i++) {
            if (null == moduleList.get(i)) {
                throw new RuntimeException("模块设置不完整(" + i + ")");
            }
        }
    }

    /**
     * @return 是否到达文本末尾
     */
    protected boolean parseLines(ICallback<SLine, Line, Flag> callback) {
        Line lastLine = null;
        SLine sLine;
        String lineString = null;
        // 若数据源有数据，则继续
        while (null != (sLine = getNextSLine()) && null != (lineString = sLine.getLineString())) {
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
            boolean needBreak = callback.onHandleLineAndFlag(sLine, curLine, mFlagParser.parse(curLine));
            if (needBreak) {
                break;
            }
            // 更改上一行对象的指向
            lastLine = curLine;
        }
        // 若数据源已没数据，则返回打断标识
        return null == lineString;
    }

    protected abstract SLine getNextSLine();

    protected interface ICallback<SLine extends ISeniorLine, Line, Flag> {
        /**
         * @param line 不为null
         * @param flag 此行对应为的标签，可为null
         * @return 是否需要中断
         */
        boolean onHandleLineAndFlag(SLine sLine, Line line, Flag flag);
    }

}
