package com.soybeany.core.common;

import java.io.IOException;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLoader<Data, RLine, Index> extends BaseModule<Data> {

    /**
     * 读取下一行对象
     *
     * @return 内容，若到末尾则返回null
     */
    public abstract RLine getNextRawLine() throws IOException;

    public abstract void onOpen(String purpose, Index index) throws IOException;

    public abstract void onClose() throws IOException;
}
