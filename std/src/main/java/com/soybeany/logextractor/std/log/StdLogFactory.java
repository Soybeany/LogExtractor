package com.soybeany.logextractor.std.log;

import com.soybeany.logextractor.core.center.SimpleLruStorage;
import com.soybeany.logextractor.core.center.SimpleUniqueLock;
import com.soybeany.logextractor.core.common.BusinessException;
import com.soybeany.logextractor.core.query.BaseLogFactory;
import com.soybeany.logextractor.sfile.data.IRenewalInfoAccessor;
import com.soybeany.logextractor.std.data.ILogStorageIdAccessor;
import com.soybeany.logextractor.std.data.Line;
import com.soybeany.logextractor.std.data.Log;
import com.soybeany.logextractor.std.data.flag.Flag;

import java.util.HashMap;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/7.
 */
public class StdLogFactory<Data extends IRenewalInfoAccessor & ILogStorageIdAccessor> extends BaseLogFactory<Line, Flag, Log, Data> {

    private static SimpleLruStorage<String, Map<String, Log>> STORAGE = new SimpleLruStorage<String, Map<String, Log>>();
    private Map<String, Log> mLogMap;
    private String mStorageId;

    @Override
    public void onStart(Data data) throws Exception {
        super.onStart(data);
        mStorageId = data.getLogStorageId();
        mLogMap = STORAGE.get(mStorageId);
        if (null == mLogMap) {
            STORAGE.put(mStorageId, mLogMap = new HashMap<String, Log>());
        }
        SimpleUniqueLock.tryAttain(hashCode() + "", mStorageId, "日志正在生成，请稍后");
    }

    @Override
    public void onFinish() throws Exception {
        super.onFinish();
        SimpleUniqueLock.release(hashCode() + "", mStorageId);
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
