package com.soybeany.log.index;

import com.soybeany.log.base.ISeniorLine;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
public interface IIndexLoader<Position, SLine extends ISeniorLine> {

    /**
     * 设置读取的起点
     */
    void setOutset(Position pos);

    /**
     * 读取下一行，若到末尾则返回null
     */
    SLine getNextSeniorLine();

}
