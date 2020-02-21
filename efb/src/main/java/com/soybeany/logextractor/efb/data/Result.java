package com.soybeany.logextractor.efb.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.soybeany.logextractor.efb.data.flag.RequestFlag;
import com.soybeany.logextractor.efb.util.TypeChecker;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.StdLog;
import com.soybeany.logextractor.std.data.StdReport;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * <br>Created by Soybeany on 2020/2/20.
 */
public class Result {

    public final List<Object> list = new LinkedList<Object>();

    public Result(StdReport report) {
        addInfo(report);
        addLogs(report);
    }

    // ****************************************内部方法****************************************

    private void addInfo(StdReport report) {
        LoadLength loadLength = new LoadLength();
        loadLength.totalScan = report.totalScan;
        loadLength.newScan = report.newScan;
        loadLength.queryLoad = report.queryLoad;
        list.add(loadLength);

        Count count = new Count();
        count.expectCount = report.expectCount;
        count.actualCount = report.actualCount;
        count.noNextDataReason = report.noNextDataReason;
        list.add(count);

        Id id = new Id();
        id.lastDataId = report.lastDataId;
        id.curDataId = report.curDataId;
        id.nextDataId = report.nextDataId;
        list.add(id);
    }

    private void addLogs(StdReport report) {
        if (null == report.logs) {
            return;
        }
        for (StdLog log : report.logs) {
            if (TypeChecker.isRequest(log.getType())) {
                list.add(getRequestLog(log));
            } else {
                list.add(new NotSupportLog());
            }
        }
    }

    private RequestLog getRequestLog(StdLog log) {
        RequestLog requestLog = new RequestLog();
        // 通用属性
        String startTime = null, endTime = null;
        if (null != log.startFlag) {
            requestLog.visit = startTime = log.startFlag.info.time;
        }
        if (null != log.endFlag) {
            endTime = log.endFlag.info.time;
            if (null == requestLog.visit) {
                requestLog.visit = endTime + "(结束)";
            }
        }
        requestLog.calculateSpend(startTime, endTime);
        requestLog.thread = log.logId;
        // 请求属性
        RequestFlag flag = (RequestFlag) log.getFlag();
        requestLog.url = flag.url;
        requestLog.param = flag.param;
        requestLog.user = flag.userNo;
        requestLog.linesToLogs(log.lines);
        return requestLog;
    }

    // ****************************************内部类****************************************

    public static class LoadLength {
        public Long totalScan;
        public Long newScan;
        public Long queryLoad;
    }

    public static class Count {
        public int expectCount;
        public int actualCount;
        public String noNextDataReason;
    }

    public static class Id {
        public String lastDataId;
        public String curDataId;
        public String nextDataId;
    }

    public static class Log {
        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd hh:mm:ss");

        public String visit;
        public String spend;
        public String thread;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String msg;
        public List<String> logs;

        public void calculateSpend(String startTime, String endTime) {
            if (null == startTime || null == endTime) {
                spend = "未知";
                return;
            }
            try {
                int sec = (int) ((DATE_FORMAT.parse(endTime).getTime() - DATE_FORMAT.parse(startTime).getTime()) / 1000);
                if (0 == sec) {
                    spend = "<1s";
                } else {
                    spend = sec + "s";
                }
            } catch (Exception e) {
                spend = "未知(时间解析异常)";
            }
        }

        public void linesToLogs(List<StdLine> lines) {
            logs = new LinkedList<String>();
            for (StdLine line : lines) {
                logs.add(line.info.time + " " + line.info.level + " " + line.content + "(" + line.info.position + ")");
            }
        }
    }

    public static class NotSupportLog extends Log {
        {
            msg = "未支持的日志";
        }
    }

    @JsonPropertyOrder({"visit", "spend", "url", "param", "user", "thread", "msg", "logs"})
    public static class RequestLog extends Log {
        public String url;
        public String param;
        public String user;
    }

    public static class TimerLog extends Log {

    }
}
