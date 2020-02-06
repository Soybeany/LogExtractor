package com.soybeany.log.index;

import com.soybeany.log.base.IParamRecipient;
import com.soybeany.log.base.IRawLine;

import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/6.
 */
public interface ICreatorFactory<Param, Index, RLine extends IRawLine, Line, Flag> extends IParamRecipient<Param> {

    List<? extends IIndexCreator<Param, Index, RLine, Line>> getLineCreators();

    List<? extends IIndexCreator<Param, Index, RLine, Flag>> getFlagCreators();
}
