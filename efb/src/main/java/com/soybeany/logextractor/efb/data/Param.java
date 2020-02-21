package com.soybeany.logextractor.efb.data;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.efb.util.TypeChecker;
import com.soybeany.logextractor.std.data.StdParam;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * <br>Created by Soybeany on 2020/2/17.
 */
public class Param extends StdParam {
    private static final String FORMAT_DATE = "yyyy-MM-dd";

    public String dir = "D:";

    public String date;
    public Time fromTime;
    public Time toTime;

    public String userNo;
    public String url;

    public Set<String> types;
    public String logId;
    public String logContainKey;
    public String logContainRegex;
    public Boolean enableIncompleteLogs;

    public long querySizeLimit = StdParam.DEFAULT_QUERY_SIZE_LIMIT;
    public int maxLineOfLogWithoutStartFlag = StdParam.DEFAULT_MAX_LINE_OF_LOG_WITHOUT_START_FLAG;
    public int logLimit = StdParam.DEFAULT_LOG_LIMIT;

    public int tryLockTimeoutSec = Integer.MAX_VALUE;

    public boolean hideUnimportantInfo;

    @Override
    public File getFileToLoad() {
        File file = new File(dir, date + ".log");
        if (!file.exists()) {
            throw new BusinessException("指定的文件不存在:" + file.getPath());
        }
        return file;
    }

    @Override
    public String getFileCharset() {
        return "utf-8";
    }

    @Override
    public long getQuerySizeLimit() {
        return querySizeLimit;
    }

    @Override
    public int getMaxLineOfLogWithoutStartFlag() {
        return maxLineOfLogWithoutStartFlag;
    }

    @Override
    public int getLogLimit() {
        return logLimit;
    }

    @Override
    public int getTryLockTimeoutSec() {
        return tryLockTimeoutSec;
    }

    @Override
    public void onCheckParams() {
        // 检查日期
        if (null == date) {
            date = new SimpleDateFormat(FORMAT_DATE).format(new Date());
        }
        // 检查时间
        if (null != fromTime && null != toTime) {
            if (fromTime.toValue(true) > toTime.toValue(true)) {
                throw new BusinessException("开始时间不能大于结束时间");
            }
        }
    }

    // ****************************************文件信息****************************************

    public Param dir(String dir) {
        this.dir = dir;
        return this;
    }

    public Param date(String date) {
        this.date = date;
        return this;
    }

    // ****************************************锁****************************************

    public Param tryLockTimeoutSec(int sec) {
        tryLockTimeoutSec = sec;
        return this;
    }

    // ****************************************索引****************************************

    /**
     * 请求开始的时间，不早于该时间
     */
    public Param fromTime(String time) {
        fromTime = new Time(time);
        return this;
    }

    /**
     * 请求开始的时间，不晚于该时间
     */
    public Param toTime(String time) {
        toTime = new Time(time);
        return this;
    }

    // ****************************************索引+过滤器****************************************

    public Param userNo(String userNo) {
        this.userNo = userNo;
        return this;
    }

    public Param url(String url) {
        this.url = url;
        return this;
    }

    // ****************************************过滤器****************************************

    /**
     * 使用“|”分隔多种类型
     */
    public Param types(String types) {
        this.types = new HashSet<String>(Arrays.asList(types.split("\\|")));
        for (String type : this.types) {
            if (!TypeChecker.ALL_TYPES.contains(type)) {
                throw new BusinessException("使用了不支持的类型:" + type);
            }
        }
        return this;
    }

    public Param logContainKey(String key) {
        logContainKey = key;
        return this;
    }

    public Param logContainRegex(String regex) {
        logContainRegex = regex;
        return this;
    }

    public Param logId(String id) {
        logId = id;
        return this;
    }

    public Param enableIncompleteLogs(boolean enable) {
        enableIncompleteLogs = enable;
        return this;
    }

    // ****************************************查询范围****************************************

    public Param querySizeLimit(long limit) {
        querySizeLimit = limit;
        return this;
    }

    public Param maxLineOfLogWithoutStartFlag(int maxLine) {
        maxLineOfLogWithoutStartFlag = maxLine;
        return this;
    }

    public Param logLimit(int limit) {
        logLimit = limit;
        return this;
    }

    // ****************************************生成结果****************************************

    public Param hideUnimportantInfo(boolean flag) {
        hideUnimportantInfo = flag;
        return this;
    }

    // ****************************************内部类****************************************

    public static class Time {
        public int hour;
        public int min;
        public int sec;

        public static int toSecValue(int hour, int min, int sec) {
            return toMinValue(hour, min) * 60 + sec;
        }

        public static int toMinValue(int hour, int min) {
            return hour * 60 + min;
        }

        /**
         * 只接受8位或者5位的时间
         */
        Time(String str) {
            int length = str.length();
            try {
                if (length < 5 || length > 8) {
                    throw new Exception("长度不对");
                }
                hour = Integer.parseInt(str.substring(0, 2));
                min = Integer.parseInt(str.substring(3, 5));
                if (length == 8) {
                    sec = Integer.parseInt(str.substring(6, 8));
                }
            } catch (Exception e) {
                throw new BusinessException("只支持hh:mm格式或者hh:mm:ss格式的时间:" + e.getMessage());
            }
        }

        public int toValue(boolean needSec) {
            return needSec ? toSecValue(hour, min, sec) : toMinValue(hour, min);
        }
    }
}
