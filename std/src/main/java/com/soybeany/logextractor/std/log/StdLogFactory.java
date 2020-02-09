package com.soybeany.logextractor.std.log;

import com.soybeany.logextractor.core.center.SimpleUniqueLock;
import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.common.ConcurrencyException;
import com.soybeany.logextractor.core.query.BaseLogFactory;
import com.soybeany.logextractor.std.data.IStdData;
import com.soybeany.logextractor.std.data.Line;
import com.soybeany.logextractor.std.data.Log;
import com.soybeany.logextractor.std.data.flag.Flag;

import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdLogFactory<Data extends IStdData> extends BaseLogFactory<Data, Line, Flag, Log> {

    private Map<String, Log> mLogMap;

    @Override
    public void onActivate(Data data) {
        super.onActivate(data);
        mLogMap = data.getLogMap();
    }

    @Override
    public void attainLock() throws ConcurrencyException {
        SimpleUniqueLock.tryAttain(mLogMap, "日志正在生成，请稍后");
    }

    @Override
    public void releaseLock() {
        SimpleUniqueLock.release(mLogMap);
    }

    @Override
    public void addLine(Line line) {
        String logId = line.info.getLogId();
        Log log = mLogMap.get(logId);
        if (null == log) {
            log = new Log(logId);
        }
        log.lines.add(line);
    }

    @Override
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
}
