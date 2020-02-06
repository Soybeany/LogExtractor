package com.soybeany.log.base;

import java.io.IOException;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public interface ILoader<Param, Range, RLine extends IRawLine> extends IParamRecipient<Param> {

    /**
     * 设置加载的范围
     */
    void setRange(Range range) throws IOException;

    /**
     * 获取下一行内容
     *
     * @return 内容，若已到末尾则返回null
     */
    String getNextLineText() throws IOException;

    /**
     * 读取下一行
     */
    RLine getNextRawLine() throws IOException;

    void onOpen() throws IOException;

    void onClose() throws IOException;
}
