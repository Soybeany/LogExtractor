package com.soybeany.log.query;

import com.google.gson.Gson;
import com.soybeany.log.base.BaseIndexCenter;
import com.soybeany.log.base.BaseLoader;
import com.soybeany.log.impl.handle.Log;
import com.soybeany.log.impl.loader.single.ISFileData;
import com.soybeany.log.impl.loader.single.SFileRange;
import com.soybeany.log.impl.loader.single.SFileRawLine;
import com.soybeany.log.impl.loader.single.SingleFileLoader;
import com.soybeany.log.impl.parser.Flag;
import com.soybeany.log.impl.parser.Line;
import com.soybeany.log.impl.parser.MetaInfo;
import com.soybeany.log.index.BaseCreatorFactory;
import com.soybeany.log.index.BaseIndexCreator;
import com.soybeany.log.index.IndexManager;
import com.soybeany.log.query.parser.BaseFlagParser;
import com.soybeany.log.query.parser.BaseLineParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
class QueryManagerTest {

    private IndexCenter mIndexCenter = new IndexCenter();

    private BaseLoader<Data, SFileRange, SFileRawLine> mLoader = new SingleFileLoader<Data>();

    @Test
    public void testIndex() throws IOException {
        Data data = new Data();
        IndexManager<Data, SFileRange, Index, SFileRawLine, Line, Flag> manager = new IndexManager<Data, SFileRange, Index, SFileRawLine, Line, Flag>(data);
        manager.setCenter(mIndexCenter);
        manager.setLoader(mLoader);
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.setCreatorFactory(new CreatorFactory());
        manager.createIndexes();
    }

    @Test
    public void testQuery() throws IOException {
        Data data = new Data();
        QueryManager<Data, SFileRange, Line, Flag, Log, Report> manager = new QueryManager<Data, SFileRange, Line, Flag, Log, Report>(data);
        manager.setCenter(mIndexCenter);
        manager.setLoader(mLoader);
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.setLogFactory(new LogFactory());
        manager.setReporter(new Reporter());
        manager.setFilterFactory(new FilterFactory());
        Report report = manager.query();
        System.out.println(report.json);
    }

    // ****************************************模块****************************************

    private static class IndexCenter extends BaseIndexCenter<Data, SFileRange, Index> {

        private final Index mIndex = new Index();

        public SFileRange getLoadRange() {
            return null;
        }

        public Index getIndex() {
            return mIndex;
        }

        public void onInit(Data data) {

        }
    }

    private static class LineParser extends BaseLineParser<Data, Line> {

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

        public void onInit(Data data) {

        }
    }

    private static class FlagParser extends BaseFlagParser<Data, Line, Flag> {

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

        public void onInit(Data data) {

        }
    }

    private static class LogFactory extends BaseLogFactory<Data, Line, Flag, Log> {

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

        public void onInit(Data data) {

        }
    }

    private static class Reporter extends BaseReporter<Data, Log, Report> {

        private static final Gson GSON = new Gson();
        private final List<Log> mResultList = new LinkedList<Log>();

        public boolean needMoreLog() {
            return true;
        }

        public void addLog(Log log) {
            mResultList.add(log);
        }

        public Report getReport() {
            return new Report(GSON.toJson(mResultList));
        }

        public void onInit(Data data) {

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

    private static class FilterFactory extends BaseFilterFactory<Data, Log> {

        public List<BaseFilter<Data, Log>> getFilters() {
            return Collections.emptyList();
        }

        public void onInit(Data data) {

        }
    }

    private static class Filter extends BaseFilter<Data, Log> {
        public boolean isFiltered(Log log) {
            return "100".equals(log.logId);
        }

        public void onInit(Data data) {

        }
    }

    private static class CreatorFactory extends BaseCreatorFactory<Data, Index, SFileRawLine, Line, Flag> {

        public List<? extends BaseIndexCreator<Data, Index, SFileRawLine, Line>> getLineCreators() {
            return Collections.singletonList(new LineIndexCreator());
        }

        public List<? extends BaseIndexCreator<Data, Index, SFileRawLine, Flag>> getFlagCreators() {
            return Collections.singletonList(new FlagIndexCreator());
        }

        public void onInit(Data data) {

        }
    }

    private static class LineIndexCreator extends BaseIndexCreator<Data, Index, SFileRawLine, Line> {
        public void onCreateIndex(Index index, SFileRawLine rLine, Line line) {
            int time = Integer.parseInt(line.info.time);
            if (null == index.time[time]) {
                index.time[time] = SFileRange.from(rLine.getStartPointer());
            }
        }

        public void onInit(Data data) {

        }
    }

    private static class FlagIndexCreator extends BaseIndexCreator<Data, Index, SFileRawLine, Flag> {
        public void onCreateIndex(Index index, SFileRawLine rLine, Flag flag) {

        }

        public void onInit(Data data) {

        }
    }

    // ****************************************模型****************************************

    private static class Data implements ISFileData {
        public File getFileToLoad() {
            return new File("D:\\source.txt");
        }

        public String getFileCharset() {
            return "utf-8";
        }
    }

    private static class FlagInfo extends Flag {
        String detail;

        FlagInfo(MetaInfo info) {
            super(info);
        }
    }

    private static class RequestFlag extends Flag {
        private static Pattern PATTERN = Pattern.compile("(\\d+) (.+) (.+)");

        String url;
        String data;
        String userNo;

        RequestFlag(FlagInfo flag) {
            super(flag);
            Matcher matcher = PATTERN.matcher(flag.detail);
            if (!matcher.find()) {
                throw new RuntimeException("无法解析请求标签");
            }
            userNo = matcher.group(1);
            url = matcher.group(2);
            data = matcher.group(3);
        }
    }

    private static class Index {
        SFileRange[] time = new SFileRange[5];
        Map<String, SFileRange> thread = new HashMap<String, SFileRange>();
        Map<String, SFileRange> user = new HashMap<String, SFileRange>();
        Map<String, SFileRange> url = new HashMap<String, SFileRange>();
    }

    private static class Report implements IReport {

        public String json;

        public String getTaskId() {
            return null;
        }

        public Report(String json) {
            this.json = json;
        }
    }
}