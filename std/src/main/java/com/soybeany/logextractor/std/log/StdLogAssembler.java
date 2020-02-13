package com.soybeany.logextractor.std.log;

import com.soybeany.logextractor.core.center.SimpleUniqueLock;
import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.query.BaseLogAssembler;
import com.soybeany.logextractor.sfile.data.IRenewalData;
import com.soybeany.logextractor.std.data.IStdLogAssemblerData;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.StdLog;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.Collection;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdLogAssembler<Param, Data extends IRenewalData & IStdLogAssemblerData> extends BaseLogAssembler<Param, StdLine, StdFlag, StdLog, Data> {

    private Map<String, StdLog> mLogMap;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);
        mLogMap = data.getLogStorage();
        SimpleUniqueLock.tryAttain(hashCode() + "", mLogMap, "日志正在生成，请稍后");
    }

    @Override
    public void onFinish() throws Exception {
        super.onFinish();
        SimpleUniqueLock.release(hashCode() + "", mLogMap);
    }

    @Override
    public StdLog addLine(StdLine line) {
        String logId = line.info.getLogId();
        StdLog log = mLogMap.get(logId);
        if (null == log) {
            log = new StdLog(logId);
        }
        log.lines.add(line);
        return null;
    }

    @Override
    public StdLog addFlag(StdFlag flag) {
        String logId = flag.info.getLogId();
        // 若为开始状态
        if (StdFlag.STATE_START.equals(flag.state)) {
            StdLog log = new StdLog(logId);
            log.startFlag = flag;
            return mLogMap.put(logId, log);
        }
        // 若为结束状态
        if (StdFlag.STATE_END.equals(flag.state)) {
            StdLog log = mLogMap.remove(logId);
            if (null != log) {
                log.endFlag = flag;
            }
            return log;
        }
        // 其它状态
        throw new BusinessException("使用了未知的状态");
    }

    @Override
    public Collection<StdLog> getIncompleteLogs() {
        return null;
    }
}
