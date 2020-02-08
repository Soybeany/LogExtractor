package com.soybeany.core.query;

import com.google.gson.Gson;
import com.soybeany.core.impl.center.MemIndexCenter;
import com.soybeany.core.impl.center.MemStorageCenter;
import com.soybeany.core.scan.BaseCreatorFactory;
import com.soybeany.core.scan.BaseIndexCreator;
import com.soybeany.sfile.accessor.SFileDataAccessor;
import com.soybeany.sfile.center.SFileIndexCenter;
import com.soybeany.sfile.data.ISFileData;
import com.soybeany.sfile.data.SFileRawLine;
import com.soybeany.sfile.loader.SingleFileLoader;
import com.soybeany.std.StdLogExtractor;
import com.soybeany.std.data.*;
import com.soybeany.std.data.flag.Flag;
import com.soybeany.std.data.flag.FlagInfo;
import com.soybeany.std.log.StdLogFactory;
import com.soybeany.std.parser.StdFlagParser;
import com.soybeany.std.parser.StdLineParser;
import com.soybeany.std.reporter.StdQueryReporter;
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
        StdLogExtractor<Data, Index, QueryReport> manager = new StdLogExtractor<Data, Index, QueryReport>();
        manager.setDataAccessor(new DataAccessor());
        manager.setStorageCenter(new MemStorageCenter<Data>());
        manager.setIndexCenter(new SFileIndexCenter<Data, Index>(new InfoProvider()));
        manager.setLoader(new SingleFileLoader<Data>());
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.setLogFactory(new StdLogFactory<Data>());
        manager.setReporter(new StdQueryReporter<Data>());
        manager.setFilterFactory(new FilterFactory());
        manager.setCreatorFactory(new CreatorFactory());
        QueryReport report = manager.find(data);
        System.out.println(new Gson().toJson(report));
        QueryReport report2 = manager.findById(report.nextDataId);
        System.out.println(new Gson().toJson(report2));
    }

    // ****************************************模块****************************************

    private static class DataAccessor extends SFileDataAccessor<Data, Index, QueryReport> {

        @Override
        public String getCurDataId(Data data) {
            return data.curDataId;
        }

        @Override
        public void setIndex(Data data, Index index) {
            data.index = index;
        }

        @Override
        public Index getIndex(Data data) {
            return data.index;
        }

        @Override
        public void setReport(Data data, QueryReport report) {
            data.report = report;
        }

        @Override
        public QueryReport getReport(Data data) {
            return data.report;
        }

        @Override
        public String getLastDataId(Data data) {
            return data.lastDataId;
        }

        @Override
        public void setLastDataId(Data data, String id) {
            data.lastDataId = id;
        }

        @Override
        public String getNextDataId(Data data) {
            return data.nextDataId;
        }

        @Override
        public void setNextDataId(Data data, String id) {
            data.nextDataId = id;
        }

        @Override
        public long getPointer(Data data) {
            return data.pointer;
        }

        @Override
        public void setPointer(Data data, long pointer) {
            data.pointer = pointer;
        }

        @Override
        public Data getNewData(Data source) {
            Data nextData = new Data();
            nextData.mLogMap = source.mLogMap;
            return nextData;
        }
    }

    private static class InfoProvider implements MemIndexCenter.IInfoProvider<Data, Index> {
        @Override
        public String getIndexKey(Data data) {
            return data.getFileToLoad().toString();
        }

        @Override
        public Index getNewIndex() {
            return new Index();
        }

        @Override
        public Index getNewIndex(Index source) {
            return source.copy(new Index());
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

    private static class Data implements ISFileData, IStdData {

        Index index;
        long pointer;
        QueryReport report;

        String lastDataId;
        final String curDataId = UUID.randomUUID().toString().replace("-", "");
        String nextDataId;

        private Map<String, Log> mLogMap = new HashMap<String, Log>();
        private List<Log> mLogList = new LinkedList<Log>();

        public File getFileToLoad() {
            return new File("D:\\source.txt");
        }

        public String getFileCharset() {
            return "utf-8";
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
        public int getLogLimit() {
            return 1;
        }
    }
}