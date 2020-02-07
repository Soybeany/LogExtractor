package com.soybeany.core.sort;

import com.google.gson.Gson;
import com.soybeany.core.LogManager;
import com.soybeany.core.index.BaseCreatorFactory;
import com.soybeany.core.index.BaseIndexCreator;
import com.soybeany.sfile.center.MemSFileIndexCenter;
import com.soybeany.sfile.center.MemStorageCenter;
import com.soybeany.sfile.data.ISFileData;
import com.soybeany.sfile.loader.SFileRange;
import com.soybeany.sfile.loader.SFileRawLine;
import com.soybeany.sfile.loader.SingleFileLoader;
import com.soybeany.std.data.Index;
import com.soybeany.std.data.Line;
import com.soybeany.std.data.Log;
import com.soybeany.std.data.flag.Flag;
import com.soybeany.std.data.flag.FlagInfo;
import com.soybeany.std.parser.StdFlagParser;
import com.soybeany.std.parser.StdLineParser;
import efb.EFBRequestFlag;
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

    private static class LineParser extends StdLineParser<Data> {
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
    }

    private static class FlagParser extends StdFlagParser<Data> {
        private static Pattern PATTERN = Pattern.compile("FLAG-(.+)-(.+):(.+)");

        {
            addFactory("客户端", new SortManagerTest.FlagFactory());
        }

        @Override
        protected FlagInfo toFlagInfo(Line line) {
            Matcher matcher = PATTERN.matcher(line.content);
            if (!matcher.find()) {
                return null;
            }
            FlagInfo flag = new FlagInfo(line.info);
            flag.state = matcher.group(1);
            flag.type = matcher.group(2);
            flag.detail = matcher.group(3);
            return flag;
        }
    }

    private static class FlagFactory extends StdFlagParser.FlagFactory {
        private static Pattern REQUEST_PATTERN = Pattern.compile("(\\d+) (.+) (.+)");

        @Override
        public Pattern getPattern() {
            return REQUEST_PATTERN;
        }

        @Override
        public Flag get(Matcher matcher, FlagInfo info) {
            EFBRequestFlag flag = new EFBRequestFlag(info);
            flag.userNo = matcher.group(1);
            flag.url = matcher.group(2);
            flag.param = matcher.group(3);
            return flag;
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
//            int time = Integer.parseInt(line.info.time);
//            if (null == index.time[time]) {
//                index.time[time] = SFileRange.from(rLine.getStartPointer());
//            }
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

    private static class Report {

        public String json;

        public Report(String json) {
            this.json = json;
        }
    }
}