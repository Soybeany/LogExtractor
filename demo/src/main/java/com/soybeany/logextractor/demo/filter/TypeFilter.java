package com.soybeany.logextractor.demo.filter;

import com.soybeany.logextractor.core.query.BaseLogFilter;
import com.soybeany.logextractor.std.data.StdLog;

import java.util.Set;

/**
 * <br>Created by Soybeany on 2020/2/20.
 */
public class TypeFilter extends BaseLogFilter<StdLog> {
    private Set<String> mTypes;

    public TypeFilter(Set<String> types) {
        mTypes = types;
    }

    @Override
    public boolean isFiltered(StdLog stdLog) {
        return !mTypes.contains(stdLog.getType());
    }
}
