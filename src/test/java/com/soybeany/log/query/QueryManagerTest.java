package com.soybeany.log.query;

import com.google.gson.Gson;
import com.soybeany.log.base.IIndexCenter;
import com.soybeany.log.base.ISeniorLine;
import com.soybeany.log.index.IIndexCreator;
import com.soybeany.log.index.IIndexLoader;
import com.soybeany.log.index.IndexManager;
import com.soybeany.log.query.parser.IFlagParser;
import com.soybeany.log.query.parser.ILineParser;
import com.soybeany.log.std.data.Flag;
import com.soybeany.log.std.data.Line;
import com.soybeany.log.std.data.Log;
import com.soybeany.log.std.data.MetaInfo;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
class QueryManagerTest {

    @Test
    public void testIndex() {
        IndexManager<PositionParam, Position, IndexParam, Index, SLine, Line, Flag> manager = new IndexManager<PositionParam, Position, IndexParam, Index, SLine, Line, Flag>(null, null);
        manager.setCenter(new IndexCenter());
        manager.setLoader(new Loader());
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.addLineCreator(new LineIndexCreator());
        manager.addFlagCreator(new FlagIndexCreator());
        manager.createIndexes();
    }

    @Test
    public void testQuery() {
        QueryManager<RangeParam, Range, Line, Flag, Log> manager = new QueryManager<RangeParam, Range, Line, Flag, Log>(null);
        manager.setCenter(new IndexCenter());
        manager.setLoader(new Loader());
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.setLogFactory(new LogFactory());
        manager.setReporter(new Reporter());
//        manager.addFilter(new Filter());
        String result = manager.getResult();
        System.out.println(result);
    }

    // ****************************************模块****************************************

    private static class IndexCenter implements IIndexCenter<PositionParam, Position, RangeParam, Range, IndexParam, Index> {

        public Position getLoadOutset(PositionParam param) {
            return null;
        }

        public Range getLoadRange(RangeParam param) {
            return null;
        }

        public Index getIndex(IndexParam param) {
            return null;
        }
    }

    private static class Loader implements ILoader<Range>, IIndexLoader<Position, SLine> {

        private static String[] LINES = {
                "100-FLAG-开始-客户端:238744 /efb/abc/def.do {param1}",
                "100-这就是100",
                "换行了",
                "再换一行",
                "101-FLAG-开始-客户端:238744 /efb/ggg/def.do {param2}",
                "100-FLAG-结束-客户端:238744 /efb/abc/def.do {param1}",
                "101-这是另外的101",
                "101-这是101的第二行日志",
                "101-FLAG-结束-客户端:238744 /efb/ggg/def.do {param2}",
        };

        private int mIndex;

        public void setRange(Range range) {

        }

        public String getNextLine() {
            if (mIndex < LINES.length) {
                return LINES[mIndex++];
            }
            return null;
        }

        public void setOutset(Position pos) {

        }

        public SLine getNextSeniorLine() {
            if (mIndex < LINES.length) {
                return new SLine(mIndex, LINES[mIndex++]);
            }
            return null;
        }
    }

    private static class LineParser implements ILineParser<Line> {

        private static Pattern PATTERN = Pattern.compile("(\\d+)-(.*)");

        public Line parse(String s) {
            Matcher matcher = PATTERN.matcher(s);
            if (!matcher.find()) {
                return null;
            }
            Line line = new Line();
            line.info.thread = matcher.group(1);
            line.content = matcher.group(2);
            return line;
        }

        public void addContent(Line line, String content) {
            line.content += "\n" + content;
        }

    }

    private static class FlagParser implements IFlagParser<Line, Flag> {

        private static Pattern PATTERN = Pattern.compile("FLAG-(.+)-(.+):(.+)");

        public Flag parse(Line line) {
            Matcher matcher = PATTERN.matcher(line.content);
            if (!matcher.find()) {
                return null;
            }
            FlagInfo flag = new FlagInfo(line.info);
            flag.state = matcher.group(1);
            flag.type = matcher.group(2);
            flag.detail = matcher.group(3);
            return FlagFactory.get(flag);
        }

    }

    private static class LogFactory implements ILogFactory<Line, Flag, Log> {

        private Map<String, Log> mLogMap = new HashMap<String, Log>();

        public Log addFlag(Flag flag) {
            String logId = flag.info.getLogId();
            // 若为开始状态
            if (Flag.STATE_START.equals(flag.state)) {
                Log log = new Log(logId);
                log.startFlag = flag;
                return mLogMap.put(logId, log);
            }
            // 若为结束状态
            if (Flag.STATE_END.equals(flag.state)) {
                Log log = mLogMap.remove(logId);
                if (null != log) {
                    log.endFlag = flag;
                }
                return log;
            }
            // 其它状态
            throw new RuntimeException("使用了未知的状态");
        }

        public void addLine(Line line) {
            Log log = mLogMap.get(line.info.getLogId());
            if (null != log) {
                log.lines.add(line);
            }
        }

    }

    private static class Reporter implements IReporter<Log> {

        private static final Gson GSON = new Gson();
        private final List<Log> mResultList = new LinkedList<Log>();

        public boolean needMoreLog() {
            return true;
        }

        public void addLog(Log log) {
            mResultList.add(log);
        }

        public String toResult() {
            return GSON.toJson(mResultList);
        }
    }

    private static class FlagFactory {

        static Flag get(FlagInfo info) {
            if (!"客户端".equals(info.type)) {
                throw new RuntimeException("使用了不支持的类型");
            }
            return new RequestFlag(info);
        }

    }

    private static class Filter implements IFilter<Log> {
        public boolean isFiltered(Log log) {
            return "100".equals(log.logId);
        }
    }

    private static class LineIndexCreator implements IIndexCreator<Index, SLine, Line> {
        public void onCreateIndex(Index index, SLine sLine, Line line) {
            int a = 2;
        }
    }

    private static class FlagIndexCreator implements IIndexCreator<Index, SLine, Flag> {
        public void onCreateIndex(Index index, SLine sLine, Flag flag) {
            int a = 2;
        }
    }

    // ****************************************模型****************************************

    private static class FlagInfo extends Flag {
        String detail;

        FlagInfo(MetaInfo info) {
            super(info);
        }
    }

    private static class RequestFlag extends Flag {
        private static Pattern PATTERN = Pattern.compile("(\\d+) (.+) (.+)");

        String url;
        String param;
        String userNo;

        RequestFlag(FlagInfo flag) {
            super(flag);
            Matcher matcher = PATTERN.matcher(flag.detail);
            if (!matcher.find()) {
                throw new RuntimeException("无法解析请求标签");
            }
            userNo = matcher.group(1);
            url = matcher.group(2);
            param = matcher.group(3);
        }
    }

    private static class RangeParam {

    }

    private static class Range {
        int start;
        int end;
    }

    private static class PositionParam {

    }

    private static class Position {
        int index;
    }

    private static class IndexParam {

    }

    private static class Index {
        Map<String, Range> thread = new HashMap<String, Range>();
        Map<String, Range> user = new HashMap<String, Range>();
        Map<String, Range> url = new HashMap<String, Range>();
    }

    private static class SLine implements ISeniorLine {
        int index;
        String line;

        SLine(int index, String line) {
            this.index = index;
            this.line = line;
        }

        public String getLineString() {
            return line;
        }
    }
}