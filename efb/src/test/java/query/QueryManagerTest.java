package query;

import com.soybeany.logextractor.core.center.MemStorageCenter;
import com.soybeany.logextractor.efb.data.Data;
import com.soybeany.logextractor.efb.data.Index;
import com.soybeany.logextractor.efb.data.Param;
import com.soybeany.logextractor.efb.data.Report;
import com.soybeany.logextractor.efb.data.flag.RequestFlag;
import com.soybeany.logextractor.efb.filter.FilterFactory;
import com.soybeany.logextractor.efb.handler.IndexHandlerFactory;
import com.soybeany.logextractor.efb.parser.FlagParser;
import com.soybeany.logextractor.efb.parser.LineParser;
import com.soybeany.logextractor.sfile.SFileLogExtractor;
import com.soybeany.logextractor.std.Loader.StdFileLoader;
import com.soybeany.logextractor.std.StdLogExtractor;
import com.soybeany.logextractor.std.assembler.StdLogAssembler;
import com.soybeany.logextractor.std.data.StdLog;
import com.soybeany.logextractor.std.data.StdReport;
import com.soybeany.logextractor.std.reporter.StdLogReporter;
import org.junit.jupiter.api.Test;

/**
 * todo 在data中放入锁
 * <br>Created by Soybeany on 2020/2/5.
 */
class QueryManagerTest {

    private int mNo;

    @Test
    public void testLog() {
        StdLogExtractor<Param, Index, Report, Data> manager = new StdLogExtractor<Param, Index, Report, Data>(Data.class, Index.class);
        manager.setIdGenerator(new SFileLogExtractor.SimpleIdGenerator());
        manager.setIndexStorageCenter(new MemStorageCenter<Index>());
        manager.setIndexHandlerFactory(new IndexHandlerFactory());
        manager.setDataStorageCenter(new MemStorageCenter<Data>());
        manager.setLoader(new StdFileLoader<Param, Index, Data>());
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.setLogAssembler(new StdLogAssembler<Param, Data>());
        manager.setReporter(new StdLogReporter<Param, Report, Data>(Report.class));
        manager.setFilterFactory(new FilterFactory());
        StdReport report = manager.find(new Param().date("20-01-17").url("query").fromTime("11:02:54").toTime("11:03:01"));
//        System.out.println(new Gson().toJson(report));
        printUrls(report);
        String reportId;
        long query = report.queryLoad;
        while (null != (reportId = report.nextDataId)) {
            report = manager.findById(reportId);
            query += report.queryLoad;
//            System.out.println(new Gson().toJson(report));
            printUrls(report);
        }
        System.out.println("totalQuery:" + query + " and count:" + mNo);
    }

    private void printUrls(StdReport report) {
        for (StdLog log : report.logs) {
            try {
                RequestFlag flag = (RequestFlag) log.endFlag;
                System.out.println(flag.info.time + "  " + flag.url);
                mNo++;
            } catch (Exception e) {
                System.out.println("异常");
            }
        }
    }
}