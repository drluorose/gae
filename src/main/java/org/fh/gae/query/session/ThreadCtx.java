package org.fh.gae.query.session;

import io.netty.util.concurrent.FastThreadLocal;
import org.fh.gae.log.SearchLog;
import org.fh.gae.query.WeightTable;
import org.fh.gae.query.index.unit.AdUnitInfo;
import org.fh.gae.query.trace.TraceBit;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于线程的session
 */
public class ThreadCtx {
    private static FastThreadLocal<Map<String, Object>> threadLocal;

    static {
        threadLocal = new FastThreadLocal<>();
    }


    public static final String KEY_TARGETING_TRACE = "keyTargetingTrace";

    public static final String KEY_LOG = "keyLog";

    public static final String KEY_IDEA = "keyIdea";

    private static Map<String, Object> initContext() {
        Map<String, Object> map = new HashMap<>();
        threadLocal.set(map);

        return map;
    }

    public static void putContext(String key, Object val) {
        Map<String, Object> ctx = threadLocal.get();
        if (null == ctx) {
            ctx = initContext();
        }

        ctx.put(key, val);
    }

    public static <T> T getContext(String key) {
        Map<String, Object> ctx = threadLocal.get();
        if (null == ctx) {
            return null;
        }

        return (T) ctx.get(key);
    }

    public static void clean() {
        threadLocal.set(null);
    }


    public static Map<Integer, TraceBit> getTraceMap() {
        Map<Integer, TraceBit> map = getContext(KEY_TARGETING_TRACE);
        if (null == map) {
            map = new HashMap<>();
            putContext(KEY_TARGETING_TRACE, map);
        }

        return map;
    }

    /**
     * 获取单元权重Map;
     *
     * @return
     */
    public static Map<Integer, Integer> getWeightMap() {
        Map<Integer, TraceBit> traceMap = getTraceMap();

        Map<Integer, Integer> weightMap = new HashMap<>();
        for (Map.Entry<Integer, TraceBit> entry : traceMap.entrySet()) {
            Integer unitId = entry.getKey();
            TraceBit bit = entry.getValue();

            int weight = WeightTable.sum(bit.getBit());
            weightMap.put(unitId, weight);
        }

        return weightMap;
    }

    public static Map<String, SearchLog.Search.Builder> getSearchLogMap() {
        Map<String, SearchLog.Search.Builder> map = getContext(KEY_LOG);
        if (null == map) {
            map = new HashMap<>();
            putContext(KEY_LOG, map);
        }

        return map;
    }

    public static void putSearchLog(String slotId, SearchLog.Search.Builder pb) {
        getSearchLogMap().put(slotId, pb);
    }

    /**
     * 创意id -> 所在的单元信息
     * @return
     */
    public static Map<String, AdUnitInfo> getIdeaMap() {
        Map<String, AdUnitInfo> map = getContext(KEY_IDEA);
        if (null == map) {
            map = new HashMap<>();
            putContext(KEY_LOG, map);
        }

        return map;
    }
}