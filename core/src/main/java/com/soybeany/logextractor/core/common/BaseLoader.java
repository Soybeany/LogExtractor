package com.soybeany.logextractor.core.common;

import java.io.IOException;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public abstract class BaseLoader<Param, Index, Data> extends BaseModule<Param, Data> {

    public static final int PROCESS_NUM = 10;

    @Override
    public int getProcessNum() {
        return PROCESS_NUM;
    }

    public abstract void onInit(String purpose, Index index) throws IOException;

    /**
     * 读取下一行对象
     *
     * @return 内容，若到末尾则返回null
     */
    public abstract String getNextLine() throws IOException;
}
