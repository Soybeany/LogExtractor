package com.soybeany.logextractor.core.common;

import java.io.IOException;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLoader<RLine, Index, Data> extends BaseModule<Data> {

    public abstract void onInit(String purpose, Index index) throws IOException;

    /**
     * 读取下一行对象
     *
     * @return 内容，若到末尾则返回null
     */
    public abstract RLine getNextRawLine() throws IOException;
}
