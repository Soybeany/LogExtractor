package com.soybeany.logextractor.efb.filter;

import com.soybeany.logextractor.efb.data.flag.RequestFlag;
import com.soybeany.logextractor.efb.util.TypeChecker;
import com.soybeany.logextractor.std.data.StdLog;

/**
 * <br>Created by Soybeany on 2020/2/19.
 */
public class UserNoFilter extends BaseKeyIgnoreCaseFilter {
    public UserNoFilter(String key) {
        super(key);
    }

    @Override
    protected String getSource2(StdLog stdLog) {
        return ((RequestFlag) stdLog.startFlag).userNo;
    }

    @Override
    protected boolean isTypeSuitable(String type) {
        return TypeChecker.isRequest(type);
    }
}
