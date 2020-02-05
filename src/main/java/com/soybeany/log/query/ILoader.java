package com.soybeany.log.query;

/**
 * <br>Created by Soybeany on 2020/2/4.
 */
public interface ILoader {

    /**
     * 获取下一行内容
     *
     * @return 内容，若已到末尾则返回null
     */
    String getNextLine();

}
