package com.edwin.shakazookeeper.exe;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentMap;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author jinming.wu
 * @date 2015-6-3
 */
public class DateWrapperHolder {

    private static int                 DEFAUL_CLEAR_DAYS = 2;

    public static String               INSTANCE_COUNTER  = "instanceCounter";

    public static String               LOCK              = "lockWrapper";

    private static Map<String, Object> map               = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    public <T> ConcurrentMap<String, DateWrapper<T>> createDateWrapper(String key) {

        ConcurrentMap<String, DateWrapper<T>> wrapperMap = null;
        wrapperMap = (ConcurrentMap<String, DateWrapper<T>>) map.get(key);
        synchronized (map) {
            if (wrapperMap == null) {
                wrapperMap = Maps.newConcurrentMap();
                map.put(key, wrapperMap);
            }
        }

        return wrapperMap;
    }

    /**
     * 去除过期的数据，防止内存泄漏（需通过线上验证）
     * 
     * @param <T>
     */
    @SuppressWarnings("unchecked")
    public <T> void removeOverdueData() {

        for (Entry<String, Object> entry : map.entrySet()) {
            List<String> needRemoves = Lists.newArrayList();
            ConcurrentMap<String, DateWrapper<T>> wrapperMap = (ConcurrentMap<String, DateWrapper<T>>) entry.getValue();
            for (Entry<String, DateWrapper<T>> subEntry : wrapperMap.entrySet()) {
                Period p = new Period(new DateTime(subEntry.getValue().getDate()), DateTime.now(), PeriodType.days());
                if (p.getDays() >= DEFAUL_CLEAR_DAYS) {
                    needRemoves.add(entry.getKey());
                }
            }
            for (String key : needRemoves) {
                wrapperMap.remove(key);
            }
        }
    }
}
