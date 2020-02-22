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
public class Report extends StdReport {

    private static String millsToSec(long mills) {
        int sec = (int) (mills / 1000);
        if (0 == sec) {
            return "<1s";
        }
        return sec + "s";
    }

    public List<Object> getInfoList(Param param) {
        List<Object> list = new LinkedList<Object>();
        addInfo(param, list);
        addLogs(list);
        return list;
    }

    // ****************************************内部方法****************************************

    private void addInfo(Param param, List<Object> list) {
        if (!param.hideUnimportantInfo) {
            LoadLength loadLength = new LoadLength();
            loadLength.totalScan = totalScan;
            loadLength.newScan = newScan;
            loadLength.queryLoad = queryLoad;
            list.add(loadLength);

            Count count = new Count();
            count.expectCount = expectCount;
            count.actualCount = actualCount;
            count.noNextDataReason = noNextDataReason;
            list.add(count);

            Spend spend = new Spend();
            spend.scanSpend = Spend.toString("扫描", scanSpend);
            spend.querySpend = Spend.toString("查询", querySpend);
            list.add(spend);
        }
        Id id = new Id();
        id.lastDataId = lastDataId;
        id.curDataId = curDataId;
        id.nextDataId = nextDataId;
        list.add(id);
    }

    private void addLogs(List<Object> list) {
        if (null == logs) {
            return;
        }
        for (StdLog log : logs) {
            if (TypeChecker.isRequest(log.getType())) {
                list.add(new RequestLog(log));
            } else {
                list.add(new DefaultLog(log));
            }
        }
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

    public static class Spend {
        public String scanSpend;
        public String querySpend;

        public static String toString(String desc, Long spend) {
            if (null == spend) {
                return "没有执行" + desc + "操作";
            }
            return millsToSec(spend);
        }
    }

    public static class Id {
        public String lastDataId;
        public String curDataId;
        public String nextDataId;
    }

    public static class Log {
        private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yy-MM-dd hh:mm:ss");

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String visit;
        @JsonInclude(JsonInclude.Include.NON_NULL)
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
                spend = millsToSec(DATE_FORMAT.parse(endTime).getTime() - DATE_FORMAT.parse(startTime).getTime());
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

    public static class DefaultLog extends Log {
        {
            msg = "无法分类的日志";
        }

        public DefaultLog(StdLog log) {
            thread = log.logId;
            linesToLogs(log.lines);
        }
    }

    @JsonPropertyOrder({"visit", "spend", "url", "param", "user", "thread", "msg", "logs"})
    public static class RequestLog extends Log {
        public String url;
        public String param;
        public String user;

        public RequestLog(StdLog log) {
            // 通用属性
            setupTime(log);
            thread = log.logId;
            setupMsg(log);
            linesToLogs(log.lines);
            // 请求属性
            RequestFlag flag = (RequestFlag) log.getFlag();
            url = flag.url;
            param = flag.param;
            user = flag.userNo;
        }

        private void setupTime(StdLog log) {
            String startTime = null, endTime = null;
            if (null != log.startFlag) {
                visit = startTime = log.startFlag.info.time;
            }
            if (null != log.endFlag) {
                endTime = log.endFlag.info.time;
                if (null == visit) {
                    visit = endTime + "(结束)";
                }
            }
            calculateSpend(startTime, endTime);
        }

        private void setupMsg(StdLog log) {
            if (null == log.startFlag) {
                msg = "缺失开始标签";
            } else if (null == log.endFlag) {
                msg = "缺失结束标签";
            }
        }
    }

    public static class TimerLog extends Log {

    }
}
