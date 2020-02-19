package com.soybeany.logextractor.efb.data;

import com.soybeany.logextractor.std.data.StdParam;

import java.io.File;

/**
 * <br>Created by Soybeany on 2020/2/17.
 */
public class Param extends StdParam {
    private File mFile;

    public String userNo;
    public String url;

    public Param(File file) {
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

    public Param userNo(String userNo) {
        if (null != userNo) {
            this.userNo = userNo.toLowerCase();
        }
        return this;
    }

    public Param url(String url) {
        if (null != url) {
            this.url = url.toLowerCase();
        }
        return this;
    }
}
