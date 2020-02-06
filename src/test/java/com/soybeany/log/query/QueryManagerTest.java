package com.soybeany.log.query;

import com.google.gson.Gson;
import com.soybeany.log.base.IIndexCenter;
import com.soybeany.log.base.ILoader;
import com.soybeany.log.impl.handle.Log;
import com.soybeany.log.impl.loader.single.SFileRange;
import com.soybeany.log.impl.loader.single.SFileRawLine;
import com.soybeany.log.impl.loader.single.SingleFileLoader;
import com.soybeany.log.impl.parser.Flag;
import com.soybeany.log.impl.parser.Line;
import com.soybeany.log.impl.parser.MetaInfo;
import com.soybeany.log.index.IIndexCreator;
import com.soybeany.log.index.IndexManager;
import com.soybeany.log.query.parser.IFlagParser;
import com.soybeany.log.query.parser.ILineParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
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

    private IndexCenter mIndexCenter = new IndexCenter();

    private ILoader<SFileRange, SFileRawLine> mLoader = new SingleFileLoader(new File("D:\\source.txt"));

    @Test
    public void testIndex() throws IOException {
        IndexManager<RangeParam, SFileRange, IndexParam, Index, SFileRawLine, Line, Flag> manager = new IndexManager<RangeParam, SFileRange, IndexParam, Index, SFileRawLine, Line, Flag>(null, null);
        manager.setCenter(mIndexCenter);
        manager.setLoader(mLoader);
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.addLineCreator(new LineIndexCreator());
        manager.addFlagCreator(new FlagIndexCreator());
        manager.createIndexes();
    }

    @Test
    public void testQuery() throws IOException {
        QueryManager<RangeParam, SFileRange, Line, Flag, Log> manager = new QueryManager<RangeParam, SFileRange, Line, Flag, Log>(null);
        manager.setCenter(mIndexCenter);
        manager.setLoader(mLoader);
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.setLogFactory(new LogFactory());
        manager.setReporter(new Reporter());
//        manager.addFilter(new Filter());
        String result = manager.getResult();
        System.out.println(result);
    }

    // ****************************************模块****************************************

    private static class IndexCenter implements IIndexCenter<RangeParam, SFileRange, IndexParam, Index> {

        private final Index mIndex = new Index();

        public SFileRange getLoadRange(RangeParam param) {
            return null;
        }

        public Index getIndex(IndexParam param) {
            return mIndex;
        }
    }

    private static class LineParser implements ILineParser<Line> {

        private static Pattern PATTERN = Pattern.compile("(\\d+)-(\\d+)-(.*)");

        public Line parse(String s) {
            Matcher matcher = PATTERN.matcher(s);
            if (!matcher.find()) {
                return null;
            }
            Line line = new Line();
            line.info.time = matcher.group(1);
            line.info.thread = matcher.group(2);
            line.content = matcher.group(3);
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

    private static class LineIndexCreator implements IIndexCreator<Index, SFileRawLine, Line> {
        public void onCreateIndex(Index index, SFileRawLine rLine, Line line) {
            int time = Integer.parseInt(line.info.time);
            if (null == index.time[time]) {
                index.time[time] = SFileRange.from(rLine.getStartPointer());
            }
        }
    }

    private static class FlagIndexCreator implements IIndexCreator<Index, SFileRawLine, Flag> {
        public void onCreateIndex(Index index, SFileRawLine rLine, Flag flag) {

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

    private static class IndexParam {

    }

    private static class Index {
        SFileRange[] time = new SFileRange[5];
        Map<String, SFileRange> thread = new HashMap<String, SFileRange>();
        Map<String, SFileRange> user = new HashMap<String, SFileRange>();
        Map<String, SFileRange> url = new HashMap<String, SFileRange>();
    }
}