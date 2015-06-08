package com.edwin.shakacore.id;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;

import com.edwin.shakacore.ShakaCoreContext;
import com.edwin.shakacore.spring.RepositoryLocator;
import com.edwin.shakapersist.dao.ShakaTaskDao;
import com.edwin.shakapersist.entity.ShakaTask;
import com.edwin.shakazookeeper.exe.DateWrapper;
import com.edwin.shakazookeeper.exe.DateWrapperHolder;
import com.google.common.collect.Maps;

/**
 * @author jinming.wu
 * @date 2015-5-26
 */
public class DefaultIDGenerator implements IDGenerator {

    // 容易造成内存溢出，使用弱引用可行？WeakHashMap
    @Getter
    public static ConcurrentMap<String, DateWrapper<AtomicInteger>> instanceCounterMap;

    public static ConcurrentMap<Integer, AtomicInteger>             taskCounterMap = Maps.newConcurrentMap();

    public static ShakaTaskDao                                      shakaTaskDao   = RepositoryLocator.getShakaTaskDao();

    public DefaultIDGenerator() {

        DateWrapperHolder warapperHolder = (DateWrapperHolder) ShakaCoreContext.getInstance().getObjectByClazz(DateWrapperHolder.class);

        instanceCounterMap = warapperHolder.createDateWrapper(DateWrapperHolder.INSTANCE_COUNTER);
    }

    @Override
    public String generateTaskID(int jobId) {

        AtomicInteger counter = taskCounterMap.get(jobId);
        if (counter == null) {
            ShakaTask task = shakaTaskDao.loadLastShakaTask(jobId);
            if (task == null) {
                counter = new AtomicInteger(0);
            } else {
                String taskId = task.getTaskId();
                int count = Integer.valueOf(taskId.substring(taskId.lastIndexOf("_") + 1, taskId.length()));
                counter = new AtomicInteger(count);
            }
            automicPut(jobId, counter);
        }

        return new TaskID(jobId, counter.incrementAndGet()).toString();
    }

    @Override
    public String generateInstanceID(String taskId) {

        DateWrapper<AtomicInteger> counter = instanceCounterMap.get(taskId);
        if (counter == null) {
            counter = new DateWrapper<AtomicInteger>(new AtomicInteger(0));
            automicPut(taskId, counter);
        }
        return new InstanceID(taskId, counter.getObject().incrementAndGet()).toString();
    }

    /**
     * 无锁
     * 
     * @param taskId
     * @param counter
     * @return
     */
    private AtomicInteger automicPut(int jobId, AtomicInteger counter) {

        AtomicInteger oldCounter = taskCounterMap.putIfAbsent(jobId, counter);
        if (oldCounter != null && oldCounter != counter) {
            counter = null;
            return oldCounter;
        }
        return counter;
    }

    /**
     * 无锁
     * 
     * @param taskId
     * @param counter
     * @return
     */
    private DateWrapper<AtomicInteger> automicPut(String taskId, DateWrapper<AtomicInteger> counter) {

        DateWrapper<AtomicInteger> oldCounter = instanceCounterMap.putIfAbsent(taskId, counter);
        if (oldCounter != null && oldCounter != counter) {
            counter = null;
            return oldCounter;
        }
        return counter;
    }
}
