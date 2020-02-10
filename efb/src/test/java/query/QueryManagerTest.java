package query;

import com.google.gson.Gson;
import com.soybeany.logextractor.core.center.MemStorageCenter;
import com.soybeany.logextractor.core.query.BaseFilter;
import com.soybeany.logextractor.core.query.BaseFilterFactory;
import com.soybeany.logextractor.core.scan.BaseCreatorFactory;
import com.soybeany.logextractor.core.scan.BaseIndexCreator;
import com.soybeany.logextractor.efb.EFBRequestFlag;
import com.soybeany.logextractor.sfile.data.SFileRawLine;
import com.soybeany.logextractor.std.Loader.StdFileLoader;
import com.soybeany.logextractor.std.StdLogExtractor;
import com.soybeany.logextractor.std.data.*;
import com.soybeany.logextractor.std.data.flag.Flag;
import com.soybeany.logextractor.std.data.flag.FlagInfo;
import com.soybeany.logextractor.std.log.StdLogFactory;
import com.soybeany.logextractor.std.parser.StdFlagParser;
import com.soybeany.logextractor.std.parser.StdLineParser;
import com.soybeany.logextractor.std.reporter.StdQueryReporter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * todo limitCount为0时需调试，dataId为null时的提示语需优化，module增加优先级设置，默认的模块跨度以10为单位
 * <br>Created by Soybeany on 2020/2/5.
 */
class QueryManagerTest {

    @Test
    public void testLog() {
        StdLogExtractor<Index, QueryReport, Data> manager = new StdLogExtractor<Index, QueryReport, Data>();
        manager.setStorageCenter(new MemStorageCenter<Index, Data>(new InfoProvider()));
        manager.setLoader(new StdFileLoader<Index, Data>());
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.setLogFactory(new StdLogFactory<Data>());
        manager.setReporter(new StdQueryReporter<Data>());
        manager.setFilterFactory(new FilterFactory());
        manager.setCreatorFactory(new CreatorFactory());
        QueryReport report = manager.find(new Data());
        System.out.println(new Gson().toJson(report));
        String reportId;
        if (null != (reportId = report.nextDataId)) {
            QueryReport nextReport = manager.findById(reportId);
            System.out.println(new Gson().toJson(nextReport));
        }
    }

    // ****************************************模块****************************************

    private static class InfoProvider implements MemStorageCenter.IIndexProvider<Index, Data> {
        @Override
        public String getIndexKey(Data data) {
            return data.getFileToLoad().toString();
        }

        @Override
        public Index getNewIndex() {
            return new Index();
        }

        @Override
        public Index getCopy(Index source) {
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

    private static class FilterFactory extends BaseFilterFactory<Log, Data> {

        public List<BaseFilter<Log, Data>> getFilters() {
            return Collections.emptyList();
        }

    }

    private static class Filter extends BaseFilter<Log, Data> {
        public boolean isFiltered(Log log) {
            return "100".equals(log.logId);
        }

    }

    private static class CreatorFactory extends BaseCreatorFactory<Index, SFileRawLine, Line, Flag, Data> {

        public List<? extends BaseIndexCreator<Index, SFileRawLine, Line, Data>> getLineCreators() {
            return Collections.singletonList(new LineIndexCreator());
        }

        public List<? extends BaseIndexCreator<Index, SFileRawLine, Flag, Data>> getFlagCreators() {
            return Collections.singletonList(new FlagIndexCreator());
        }

    }

    private static class LineIndexCreator extends BaseIndexCreator<Index, SFileRawLine, Line, Data> {
        public void onCreateIndex(Index index, SFileRawLine rLine, Line line) {
//            int time = Integer.parseInt(line.info.time);
//            if (null == index.time[time]) {
//                index.time[time] = SFileRange.from(rLine.getStartPointer());
//            }
        }

    }

    private static class FlagIndexCreator extends BaseIndexCreator<Index, SFileRawLine, Flag, Data> {
        public void onCreateIndex(Index index, SFileRawLine rLine, Flag flag) {

        }

    }

    // ****************************************模型****************************************

    public static class Data extends StdData<Index, QueryReport> {

        static {
            setIdGenerator(new SimpleIdGenerator());
        }

        public File getFileToLoad() {
            return new File("D:\\source.txt");
        }

        public String getFileCharset() {
            return "utf-8";
        }

        @Override
        public int getLogLimit() {
            return 1;
        }
    }
}