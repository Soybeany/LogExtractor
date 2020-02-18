package query;

import com.google.gson.Gson;
import com.soybeany.logextractor.core.center.MemStorageCenter;
import com.soybeany.logextractor.efb.EFBFlagParser;
import com.soybeany.logextractor.efb.EFBLineParser;
import com.soybeany.logextractor.efb.data.EFBData;
import com.soybeany.logextractor.efb.data.EFBIndex;
import com.soybeany.logextractor.efb.data.EFBParam;
import com.soybeany.logextractor.efb.data.EFBReport;
import com.soybeany.logextractor.sfile.SFileLogExtractor;
import com.soybeany.logextractor.std.Loader.StdFileLoader;
import com.soybeany.logextractor.std.StdLogExtractor;
import com.soybeany.logextractor.std.assembler.StdLogAssembler;
import com.soybeany.logextractor.std.data.StdReport;
import com.soybeany.logextractor.std.reporter.StdLogReporter;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * todo 在data中放入锁
 * <br>Created by Soybeany on 2020/2/5.
 */
class QueryManagerTest {

    @Test
    public void testLog() {
        StdLogExtractor<EFBParam, EFBIndex, EFBReport, EFBData> manager = new StdLogExtractor<EFBParam, EFBIndex, EFBReport, EFBData>(EFBData.class, EFBIndex.class);
        manager.setIdGenerator(new SFileLogExtractor.SimpleIdGenerator());
        manager.setIndexStorageCenter(new MemStorageCenter<EFBIndex>());
        manager.setDataStorageCenter(new MemStorageCenter<EFBData>());
        manager.setLoader(new StdFileLoader<EFBParam, EFBIndex, EFBData>());
        manager.setLineParser(new EFBLineParser());
        manager.setFlagParser(new EFBFlagParser());
        manager.setLogAssembler(new StdLogAssembler<EFBParam, EFBData>());
        manager.setReporter(new StdLogReporter<EFBParam, EFBReport, EFBData>(EFBReport.class));
//        manager.setFilterFactory(new LogFilterFactory());
        StdReport report = manager.find(new EFBParam(new File("D:\\source2.log")));
        System.out.println(new Gson().toJson(report));
        String reportId;
        long query = report.queryLoad;
        while (null != (reportId = report.nextDataId)) {
            report = manager.findById(reportId);
            query += report.queryLoad;
            System.out.println(new Gson().toJson(report));
        }
        System.out.println("totalQuery:" + query);
    }

}