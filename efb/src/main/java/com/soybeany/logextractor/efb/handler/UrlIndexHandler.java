package com.soybeany.logextractor.efb.handler;

import com.soybeany.logextractor.efb.data.Index;
import com.soybeany.logextractor.efb.data.Param;
import com.soybeany.logextractor.efb.data.flag.RequestFlag;
import com.soybeany.logextractor.efb.util.TypeChecker;
import com.soybeany.logextractor.sfile.data.SFileRange;
import com.soybeany.logextractor.std.data.StdLine;
import com.soybeany.logextractor.std.data.flag.StdFlag;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <br>Created by Soybeany on 2020/2/18.
 */
public class UrlIndexHandler extends BaseIndexHandler {

    @Override
    public List<SFileRange> getRangeStrict(Param param, Index index) {
        if (null == param.url) {
            return null;
        }
        List<SFileRange> result = new LinkedList<SFileRange>();
        for (Map.Entry<String, LinkedList<SFileRange>> entry : index.url.entrySet()) {
            if (entry.getKey().contains(param.url)) {
                result.addAll(entry.getValue());
            }
        }
        return result;
    }

    @Override
    public void onCreateIndexWithLine(Index index, StdLine stdLine, SFileRange lineRange) {
        // 留空
    }

    @Override
    public void onCreateIndexWithFlag(Index index, StdFlag stdFlag, SFileRange flagRange) {
        if (!TypeChecker.isRequest(stdFlag.type)) {
            return;
        }
        addIndexValue(index.url, ((RequestFlag) stdFlag).url.toLowerCase(), stdFlag, flagRange);
    }

    private static class Node implements Comparable<Node> {
        long value;
        String state;

        public Node(long value, String state) {
            this.value = value;
            this.state = state;
        }

        @Override
        public int compareTo(Node o) {
            return value > o.value ? 1 : value < o.value ? -1 : 0;
        }
    }
}
