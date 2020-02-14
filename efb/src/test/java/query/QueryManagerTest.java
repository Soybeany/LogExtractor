package query;

import com.google.gson.Gson;
import com.soybeany.logextractor.core.center.MemStorageCenter;
import com.soybeany.logextractor.core.query.BaseFilter;
import com.soybeany.logextractor.core.query.BaseFilterFactory;
import com.soybeany.logextractor.core.scan.BaseIndexCreator;
import com.soybeany.logextractor.core.scan.BaseIndexCreatorFactory;
import com.soybeany.logextractor.efb.EFBRequestFlag;
import com.soybeany.logextractor.sfile.SFileLogExtractor;
import com.soybeany.logextractor.std.Loader.StdFileLoader;
import com.soybeany.logextractor.std.StdLogExtractor;
import com.soybeany.logextractor.std.data.*;
import com.soybeany.logextractor.std.data.flag.StdFlag;
import com.soybeany.logextractor.std.data.flag.StdFlagInfo;
import com.soybeany.logextractor.std.log.StdLogAssembler;
import com.soybeany.logextractor.std.parser.StdFlagParser;
import com.soybeany.logextractor.std.parser.StdLineParser;
import com.soybeany.logextractor.std.reporter.StdLogReporter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * todo module增加优先级设置，默认的模块跨度以10为单位；将IndexCreator(创建者)与使用者放到同一个接口中；处理未组装的日志
 * <br>Created by Soybeany on 2020/2/5.
 */
class QueryManagerTest {

    @Test
    public void testLog() {
        StdLogExtractor<Param, StdIndex, StdReport, Data> manager = new StdLogExtractor<Param, StdIndex, StdReport, Data>(Data.class, StdIndex.class);
        manager.setIdGenerator(new SFileLogExtractor.SimpleIdGenerator());
        manager.setIndexStorageCenter(new MemStorageCenter<StdIndex>());
        manager.setDataStorageCenter(new MemStorageCenter<Data>());
        manager.setLoader(new StdFileLoader<Param, StdIndex, Data>());
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.setLogFactory(new StdLogAssembler<Param, Data>());
        manager.setReporter(new StdLogReporter<Param, Data>());
        manager.setFilterFactory(new FilterFactory());
        manager.setCreatorFactory(new IndexCreatorFactory());
        StdReport report = manager.find(new Param());
        System.out.println(new Gson().toJson(report));
        String reportId;
        while (null != (reportId = report.nextDataId)) {
            report = manager.findById(reportId);
            System.out.println(new Gson().toJson(report));
        }
    }

    // ****************************************模块****************************************

    private static class LineParser extends StdLineParser<Param, Data> {
        private static Pattern PATTERN = Pattern.compile("(\\d+)-(\\d+)-(.*)");

        public StdLine parse(String s) {
            Matcher matcher = PATTERN.matcher(s);
            if (!matcher.find()) {
                return null;
            }
            StdLine line = new StdLine();
            line.info.time = matcher.group(1);
            line.info.thread = matcher.group(2);
            line.content = matcher.group(3);
            return line;
        }
    }

    private static class FlagParser extends StdFlagParser<Param, Data> {
        private static Pattern PATTERN = Pattern.compile("FLAG-(.+)-(.+):(.+)");

        {
            addFactory("客户端", new QueryManagerTest.FlagFactory());
        }

        @Override
        protected StdFlagInfo toFlagInfo(StdLine line) {
            Matcher matcher = PATTERN.matcher(line.content);
            if (!matcher.find()) {
                return null;
            }
            StdFlagInfo flag = new StdFlagInfo(line.info);
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
        public StdFlag get(Matcher matcher, StdFlagInfo info) {
            EFBRequestFlag flag = new EFBRequestFlag(info);
            flag.userNo = matcher.group(1);
            flag.url = matcher.group(2);
            flag.param = matcher.group(3);
            return flag;
        }
    }

    private static class FilterFactory extends BaseFilterFactory<Param, StdLog, Data> {

        public List<BaseFilter<Param, StdLog, Data>> getFilters() {
            return Collections.emptyList();
        }

    }

    private static class Filter extends BaseFilter<Param, StdLog, Data> {
        public boolean isFiltered(StdLog log) {
            return "100".equals(log.logId);
        }

    }

    private static class IndexCreatorFactory extends BaseIndexCreatorFactory<Param, StdIndex, StdLine, StdFlag, Data> {

        public List<? extends BaseIndexCreator<Param, StdIndex, StdLine, Data>> getLineIndexCreators() {
            return Collections.singletonList(new LineIndexCreator());
        }

        public List<? extends BaseIndexCreator<Param, StdIndex, StdFlag, Data>> getFlagIndexCreators() {
            return Collections.singletonList(new FlagIndexCreator());
        }

    }

    private static class LineIndexCreator extends BaseIndexCreator<Param, StdIndex, StdLine, Data> {
        public void onCreateIndex(StdIndex index, StdLine line) {
//            int time = Integer.parseInt(line.info.time);
//            if (null == index.time[time]) {
//                index.time[time] = SFileRange.from(rLine.getStartPointer());
//            }
        }

    }

    private static class FlagIndexCreator extends BaseIndexCreator<Param, StdIndex, StdFlag, Data> {
        public void onCreateIndex(StdIndex index, StdFlag flag) {

        }

    }

    // ****************************************模型****************************************

    public static class Param implements IStdParam, IStdReporterParam {

        public File getFileToLoad() {
            return new File("D:\\source.txt");
        }

        public String getFileCharset() {
            return "utf-8";
        }

        @Override
        public int getLogLimit() {
            return 2;
        }

        @Override
        public String getIndexId() {
            return getFileToLoad().toString();
        }

        @Override
        public long getLoadSizeLimit() {
            return 120;
        }
    }

    public static class Data extends StdData<Param, StdIndex, StdReport> {
    }
}