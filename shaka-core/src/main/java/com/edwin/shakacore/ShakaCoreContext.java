package com.edwin.shakacore;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;

import org.quartz.Scheduler;
import org.springframework.beans.BeanUtils;

import com.edwin.shakacore.quartz.QuartzShakaJob;
import com.edwin.shakautils.IPHelper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public class ShakaCoreContext {

    private static ShakaCoreContext           instance       = new ShakaCoreContext();

    @Getter
    private Map<Integer, QuartzShakaJob>      qtzShakaJobMap = Maps.newConcurrentMap();

    private ConcurrentHashMap<String, Object> instanceMap    = new ConcurrentHashMap<String, Object>();

    private String                            hostIP;

    public static final String                SCHEDULER      = "scheduler";

    private ShakaCoreContext() {
        hostIP = IPHelper.getHostIP();
    }

    public Object getObjectByClazz(Class<?> clazz) {

        Preconditions.checkNotNull(clazz, "Clazz should not be null. ");

        Object object = instanceMap.get(clazz.toString());
        if (object == null) {
            object = BeanUtils.instantiateClass(clazz);
            Object old = instanceMap.putIfAbsent(clazz.toString(), object);
            if (old != null) {
                object = null;
                return old;
            }
        }
        return object;
    }

    public void putToMap(Object obj) {
        instanceMap.put(obj.getClass().toString(), obj);
    }

    public void put(String key, Object value) {
        instanceMap.put(key, value);
    }

    public void putIfAbsent(String key, Object value) {
        Object old = instanceMap.putIfAbsent(key, value);
        if (old != null && old != value) {

            // 销毁多余创建对象
            value = null;
        }
    }

    public static ShakaCoreContext getInstance() {
        return instance;
    }

    public String getHostIP() {
        return hostIP;
    }

    public Scheduler getScheduler() {
        return (Scheduler) instanceMap.get(SCHEDULER);
    }

    public void destroy() {
        if (instanceMap != null) {
            instanceMap.clear();
        }
    }
}
