package com.soybeany.log.sort;

import com.google.gson.Gson;
import com.soybeany.impl.center.MemSFileIndexCenter;
import com.soybeany.impl.center.MemStorageCenter;
import com.soybeany.impl.data.IIndex;
import com.soybeany.impl.data.ISFileData;
import com.soybeany.impl.handle.Log;
import com.soybeany.impl.loader.SFileRange;
import com.soybeany.impl.loader.SFileRawLine;
import com.soybeany.impl.loader.SingleFileLoader;
import com.soybeany.impl.parser.Flag;
import com.soybeany.impl.parser.Line;
import com.soybeany.impl.parser.MetaInfo;
import com.soybeany.log.LogManager;
import com.soybeany.log.index.BaseCreatorFactory;
import com.soybeany.log.index.BaseIndexCreator;
import com.soybeany.log.sort.parser.BaseFlagParser;
import com.soybeany.log.sort.parser.BaseLineParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
class SortManagerTest {

    @Test
    public void testLog() throws Exception {
        Data data = new Data();
        data.end = 200;
        LogManager<Data, SFileRange, Index, SFileRawLine, Line, Flag, Log, Report> manager = new LogManager<Data, SFileRange, Index, SFileRawLine, Line, Flag, Log, Report>();
        manager.setStorageCenter(new MemStorageCenter<Data, Report>());
        manager.setIndexCenter(new MemSFileIndexCenter<Data, SFileRange, Index>(new MemSFileIndexCenterCallback()));
        manager.setLoader(new SingleFileLoader<Data>());
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.setLogFactory(new LogFactory());
        manager.setReporter(new Reporter());
        manager.setFilterFactory(new FilterFactory());
        manager.setCreatorFactory(new CreatorFactory());
        Report report = manager.find("abc", data);
        System.out.println(report.json);
    }

    // ****************************************模块****************************************

    private static class MemSFileIndexCenterCallback implements MemSFileIndexCenter.ICallback<Data, SFileRange, Index> {

        @Override
        public SFileRange getLoadRange(String purpose, Index index, Data data) {
            if (SortManager.PURPOSE.equals(purpose)) {
                return SFileRange.between(data.start, data.end);
            }
            return null;
        }

        @Override
        public String getIndexKey(Data data) {
            return data.getFileToLoad().toString();
        }

        @Override
        public Index getNewIndex() {
            return new Index();
        }
    }

    private static class LineParser extends BaseLineParser<Data, SFileRawLine, Line> {

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

        @Override
        public String getLineText(SFileRawLine rLine) {
            return rLine.getLineText();
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

    }

    private static class Filter extends BaseFilter<Data, Log> {
        public boolean isFiltered(Log log) {
            return "100".equals(log.logId);
        }

    }

    private static class CreatorFactory extends BaseCreatorFactory<Data, Index, SFileRawLine, Line, Flag> {

        public List<? extends BaseIndexCreator<Data, Index, SFileRawLine, Line>> getLineCreators() {
            return Collections.singletonList(new LineIndexCreator());
        }

        public List<? extends BaseIndexCreator<Data, Index, SFileRawLine, Flag>> getFlagCreators() {
            return Collections.singletonList(new FlagIndexCreator());
        }

    }

    private static class LineIndexCreator extends BaseIndexCreator<Data, Index, SFileRawLine, Line> {
        public void onCreateIndex(Index index, SFileRawLine rLine, Line line) {
            int time = Integer.parseInt(line.info.time);
            if (null == index.time[time]) {
                index.time[time] = SFileRange.from(rLine.getStartPointer());
            }
        }

    }

    private static class FlagIndexCreator extends BaseIndexCreator<Data, Index, SFileRawLine, Flag> {
        public void onCreateIndex(Index index, SFileRawLine rLine, Flag flag) {

        }

    }

    // ****************************************模型****************************************

    private static class Data implements ISFileData {
        long start;
        long end = Long.MAX_VALUE;

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

    private static class Index implements IIndex<Index> {
        SFileRange[] time = new SFileRange[5];
        Map<String, SFileRange> thread = new HashMap<String, SFileRange>();
        Map<String, SFileRange> user = new HashMap<String, SFileRange>();
        Map<String, SFileRange> url = new HashMap<String, SFileRange>();

        @Override
        public Index copy() {
            return this;
        }
    }

    private static class Report {

        public String json;

        public Report(String json) {
            this.json = json;
        }
    }
}