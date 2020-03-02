package query;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soybeany.logextractor.efb.data.Data;
import com.soybeany.logextractor.efb.data.Index;
import com.soybeany.logextractor.efb.data.Param;
import com.soybeany.logextractor.efb.data.ReportDTO;
import com.soybeany.logextractor.efb.filter.FilterFactory;
import com.soybeany.logextractor.efb.handler.IndexHandlerFactory;
import com.soybeany.logextractor.efb.parser.FlagParser;
import com.soybeany.logextractor.efb.parser.LineParser;
import com.soybeany.logextractor.sfile.SFileLogExtractor;
import com.soybeany.logextractor.std.Loader.StdFileLoader;
import com.soybeany.logextractor.std.StdLogExtractor;
import com.soybeany.logextractor.std.assembler.StdLogAssembler;
import com.soybeany.logextractor.std.center.StdStorageCenter;
import com.soybeany.logextractor.std.data.StdReport;
import com.soybeany.logextractor.std.reporter.StdLogReporter;
import org.junit.jupiter.api.Test;

/**
 * <br>Created by Soybeany on 2020/2/5.
 */
class QueryManagerTest {

    private int mNo;

    @Test
    public void testLog() throws Exception {
        Param param = new Param().date("2020-01-17").types("管理端|客户端").url("query")
                .fromTime("11:01:57").toTime("11:03:01");
//        Param param = new Param().date("2020-01-17").logContainKey("未能在目录").maxLineOfLogWithoutStartFlag(10);
        StdLogExtractor<Param, Index, StdReport, Data> manager = new StdLogExtractor<Param, Index, StdReport, Data>(Data.class, Index.class);
        manager.setIdGenerator(new SFileLogExtractor.SimpleIdGenerator());
        manager.setIndexStorageCenter(new StdStorageCenter<Index>());
        manager.setIndexHandlerFactory(new IndexHandlerFactory());
        manager.setDataStorageCenter(new StdStorageCenter<Data>());
        manager.setLoader(new StdFileLoader<Param, Index, Data>());
        manager.setLineParser(new LineParser());
        manager.setFlagParser(new FlagParser());
        manager.setLogAssembler(new StdLogAssembler<Param, Data>());
        manager.setReporter(new StdLogReporter<Param, StdReport, Data>(StdReport.class));
        manager.setFilterFactory(new FilterFactory());
        StdReport report = manager.find(param);
        printReport(param, report);
        StdReport report2 = manager.find(param);
        printReport(param, report2);
//        String reportId;
//        long query = report.queryLoad;
//        while (null != (reportId = report.nextDataId)) {
//            report = manager.findById(reportId);
//            query += report.queryLoad;
//            printReport(param, report);
//        }
//        System.out.println("totalQuery:" + query + " and count:" + mNo);
    }

    private void printReport(Param param, StdReport report) {
        String json;
        try {
            json = new ObjectMapper().writeValueAsString(new ReportDTO(report).getInfoList(param));
        } catch (JsonProcessingException e) {
            json = "转化异常";
        }
        System.out.println(json);
    }

}