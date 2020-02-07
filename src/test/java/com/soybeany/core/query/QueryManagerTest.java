package com.soybeany.core.query;

import com.google.gson.Gson;
import com.soybeany.core.LogManager;
import com.soybeany.core.common.BusinessException;
import com.soybeany.core.scan.BaseCreatorFactory;
import com.soybeany.core.scan.BaseIndexCreator;
import com.soybeany.sfile.center.MemSFileIndexCenter;
import com.soybeany.sfile.center.MemStorageCenter;
import com.soybeany.sfile.data.ISFileData;
import com.soybeany.sfile.loader.SFileRange;
import com.soybeany.sfile.loader.SFileRawLine;
import com.soybeany.sfile.loader.SingleFileLoader;
import com.soybeany.std.data.*;
import com.soybeany.std.data.flag.Flag;
import com.soybeany.std.data.flag.FlagInfo;
import com.soybeany.std.parser.StdFlagParser;
import com.soybeany.std.parser.StdLineParser;
import com.soybeany.std.reporter.StdReporter;
import efb.EFBRequestFlag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
class QueryManagerTest {

    @Test
    public void testLog() throws Exception {
        Data data = new Data();
        LogManager<Data, SFileRange, Index, SFileRawLine, Line, Flag, Log, Report> manager = new LogManager<Data, SFileRange, Index, SFileRawLine, Line, Flag, Log, Report>();
        manager.setDataIdAccessor(new DataAccessor());
        manager.setStorageCenter(new MemStorageCenter<Data, Report>());
        manager.setIndexCenter(new MemSFileIndexCenter<Data, SFileRange, Index>(new MemSFileIndexCenterCallback()));
        manager.setLoader(new SingleFileLoader<Data>());
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.setLogFactory(new LogFactory());
        manager.setReporter(new StdReporter<Data>());
        manager.setFilterFactory(new FilterFactory());
        manager.setCreatorFactory(new CreatorFactory());
//        manager.createIndexes(data);
        Report report = manager.find(data);
        System.out.println(new Gson().toJson(report));
        Report report2 = manager.findById(report.nextDataId);
        System.out.println(new Gson().toJson(report2));
    }

    // ****************************************模块****************************************

    private static class DataAccessor extends BaseDataAccessor<Data> {
        @Override
        public String getCurDataId(Data data) {
            return data.curDataId;
        }

        @Override
        public void setNextDataId(Data data, String dataId) {
            data.nextDataId = dataId;
        }

        @Override
        public Data getNextData(Data data) {
            Data nextData = new Data();
            nextData.lastDataId = data.curDataId;
            nextData.setLoadRange(SFileRange.between(data.getLoadRange().end, Long.MAX_VALUE));
            nextData.mLogMap = data.mLogMap;
            return nextData;
        }
    }

    private static class MemSFileIndexCenterCallback implements MemSFileIndexCenter.ICallback<Data, SFileRange, Index> {

        @Override
        public SFileRange getLoadRange(String purpose, Index index, Data data) {
            SFileRange range;
            if (QueryManager.PURPOSE.equals(purpose) && null != (range = data.getLoadRange())) {
                return SFileRange.between(range.start, range.end);
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
            addFactory("客户端", new QueryManagerTest.FlagFactory());
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
            throw new BusinessException("使用了未知的状态");
        }

        @Override
        public void setLock() {

        }

        public void addLine(Line line) {
            Log log = mLogMap.get(line.info.getLogId());
            if (null != log) {
                log.lines.add(line);
            }
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

    private static class Data implements ISFileData, ILogData, IReportData {

        String lastDataId;
        final String curDataId = UUID.randomUUID().toString().replace("-", "");
        String nextDataId;

        private Map<String, Log> mLogMap = new HashMap<String, Log>();
        private List<Log> mLogList = new LinkedList<Log>();
        private SFileRange mRange;

        public File getFileToLoad() {
            return new File("D:\\source.txt");
        }

        public String getFileCharset() {
            return "utf-8";
        }

        @Override
        public SFileRange getLoadRange() {
            return mRange;
        }

        @Override
        public void setLoadRange(SFileRange range) {
            mRange = range;
        }

        @Override
        public Map<String, Log> getLogMap() {
            return mLogMap;
        }

        @Override
        public List<Log> getLogList() {
            return mLogList;
        }

        @Override
        public int getLimitCount() {
            return 1;
        }

        @Override
        public String getLastDataId() {
            return lastDataId;
        }

        @Override
        public String getCurDataId() {
            return curDataId;
        }

        @Override
        public String getNextDataId() {
            return nextDataId;
        }
    }
}