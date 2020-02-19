package com.soybeany.logextractor.efb.handler;

import com.soybeany.logextractor.efb.data.Data;
import com.soybeany.logextractor.efb.data.Index;
import com.soybeany.logextractor.efb.data.Param;
import com.soybeany.logextractor.sfile.handler.ISFileIndexHandler;
import com.soybeany.logextractor.sfile.handler.SFileIndexHandlerFactory;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.Arrays;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class IndexHandlerFactory extends SFileIndexHandlerFactory<Param, Index, StdLine, StdFlag, Data> {
    @Override
    public List<? extends ISFileIndexHandler<Param, Index, StdLine, StdFlag>> getHandlerList() {
        return Arrays.asList(new TimeIndexHandler(), new UrlIndexHandler(), new UserNoIndexHandler());
    }
}
