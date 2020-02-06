package com.soybeany.log.base;

import java.io.IOException;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLoader<Data, Range, RLine extends IRawLine> extends Module<Data> {

    /**
     * 获取下一行内容
     *
     * @return 内容，若已到末尾则返回null
     */
    public abstract String getNextLineText() throws IOException;

    /**
     * 读取下一行
     */
    public abstract RLine getNextRawLine() throws IOException;

    public abstract void onOpen(Range range) throws IOException;

    public abstract void onClose() throws IOException;
}
