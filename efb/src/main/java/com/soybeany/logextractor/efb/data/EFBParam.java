package com.soybeany.logextractor.efb.data;

import com.soybeany.logextractor.std.data.StdParam;

import java.io.File;

/**
 * <br>Created by Soybeany on 2020/2/17.
 */
public class EFBParam extends StdParam {
    private File mFile;

    public String userNo;
    public String url;

    public EFBParam(File file) {
        mFile = file;
    }

    @Override
    public File getFileToLoad() {
        return mFile;
    }

    @Override
    public String getFileCharset() {
        return "utf-8";
    }

//    @Override
//    public int getLogLimit() {
//        return 2;
//    }

//    @Override
//    public long getLoadSizeLimit() {
//        return 1000;
//    }

    public EFBParam userNo(String userNo) {
        this.userNo = userNo;
        return this;
    }

    public EFBParam url(String url) {
        this.url = url;
        return this;
    }
}
