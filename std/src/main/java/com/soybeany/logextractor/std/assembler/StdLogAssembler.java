package com.soybeany.logextractor.std.assembler;

import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.query.BaseLogAssembler;
import com.soybeany.logextractor.sfile.data.IRenewalData;
import com.soybeany.logextractor.std.data.IStdLogAssemblerData;
import com.soybeany.logextractor.std.data.IStdLogAssemblerParam;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.StdLog;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.Collection;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdLogAssembler<Param extends IStdLogAssemblerParam, Data extends IRenewalData & IStdLogAssemblerData> extends BaseLogAssembler<Param, StdLine, StdFlag, StdLog, Data> {

    private Map<String, StdLog> mLogMap;
    private int mMaxLineOfLogWithoutStartFlag;

    @Override
    public void onStart(Param param, Data data) throws Exception {
        super.onStart(param, data);
        mLogMap = data.getLogStorage();
        mMaxLineOfLogWithoutStartFlag = param.getMaxLineOfLogWithoutStartFlag();
    }

    @Override
    public StdLog addLine(StdLine line) {
        String logId = line.info.getLogId();
        StdLog log = mLogMap.get(logId);
        if (null == log) {
            mLogMap.put(logId, log = new StdLog(logId));
        }
        log.lines.add(line);
        // 若是无开始标识的日志，且日志行数已超出设定，则弹出
        if (null == log.startFlag && log.lines.size() >= mMaxLineOfLogWithoutStartFlag) {
            return mLogMap.remove(logId);
        }
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
        return mLogMap.values();
    }
}
