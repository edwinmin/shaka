package com.edwin.shakazookeeper.operator;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.edwin.shakazookeeper.MachineType;
import com.edwin.shakazookeeper.client.ZKClient;
import com.edwin.shakazookeeper.exe.DateWrapper;
import com.edwin.shakazookeeper.exe.DateWrapperHolder;
import com.edwin.shakazookeeper.exe.ExeContext;
import com.edwin.shakazookeeper.exe.ExecuteException;
import com.edwin.shakazookeeper.exe.ZKStatus;
import com.edwin.shakazookeeper.listener.KillWatcher;
import com.google.common.base.Preconditions;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public class ShakaScheduleZKOperator extends ShakaHeatBeatZKOperator implements ScheduleZKOperator {

    private static final String                             SCHEDULE = "schedule";

    private static final String                             NEW      = "new";

    private static final String                             DELETE   = "delete";

    private static final String                             RUN      = "run";

    private static ConcurrentMap<String, DateWrapper<Lock>> instanceLockMap;

    public ShakaScheduleZKOperator() throws Exception {
        super();
        DateWrapperHolder warapperHolder = new DateWrapperHolder();
        instanceLockMap = warapperHolder.createDateWrapper(DateWrapperHolder.LOCK);
    }

    public ShakaScheduleZKOperator(ZKClient zkClient) {
        super(zkClient);
    }

    @Override
    public void register(MachineType machineType, String machineIP) throws Exception {

        super.register(machineType, machineIP);

        if (machineType == MachineType.AGENT) {
            zkClient.create(getPath(SHAKA, SCHEDULE, machineIP), new byte[0]);
            zkClient.create(getPath(SHAKA, SCHEDULE, machineIP, NEW), new byte[0]);
            zkClient.create(getPath(SHAKA, SCHEDULE, machineIP, DELETE), new byte[0]);
            zkClient.create(getPath(SHAKA, SCHEDULE, machineIP, RUN), new byte[0]);
        }
    }

    @Override
    public void executeTask(ExeContext context) throws Exception {

        try {
            this.execute(context);
        } catch (ExecuteException e) {
            if (e.getMessage().contains("Lost connection")) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                    throw e;
                }
                this.execute(context);
            } else {
                throw e;
            }
        }
    }

    private void execute(ExeContext context) throws Exception {

        Preconditions.checkNotNull(context, "Execute context must not be null! ");

        String instanceId = context.getInstanceId();
        String agentIP = context.getAgentIP();
        String path = getPath(SHAKA, SCHEDULE, agentIP, instanceId);
        Lock lock = getLock(instanceId);
        try {
            lock.lock();
            ExeContext runContext = (ExeContext) zkClient.getObject(path, null);
            if (runContext != null) {
                throw new ExecuteException("InstanceId " + instanceId + " is scheduling...");
            }
            context.setZkStatus(ZKStatus.SCHEDULED.code);
            zkClient.create(getPath(SHAKA, SCHEDULE, agentIP, instanceId), context);
            zkClient.create(getPath(SHAKA, SCHEDULE, agentIP, NEW, instanceId), new byte[0]);
        } finally {
            lock.unlock();
        }
    }

    private Lock getLock(String instanceId) {

        synchronized (instanceLockMap) {
            DateWrapper<Lock> lock = instanceLockMap.get(instanceId);
            if (lock == null) {
                lock = new DateWrapper<Lock>(new ReentrantLock());
                instanceLockMap.put(instanceId, lock);
            }
            return lock.getObject();
        }
    }

    @Override
    public void killTask(ExeContext context) throws Exception {

        String instanceId = context.getInstanceId();
        String agentIP = context.getAgentIP();
        Lock lock = getLock(instanceId);
        String path = getPath(SHAKA, SCHEDULE, agentIP, instanceId);
        try {

            lock.lock();

            ExeContext runContext = (ExeContext) zkClient.getObject(path, null);
            if (runContext == null || runContext.getZkStatus() == null
                || runContext.getZkStatus() != ZKStatus.RUNNING.code) {
                throw new ExecuteException("InstanceId " + instanceId + " can't be killed. ");
            }

            Condition condition = lock.newCondition();
            KillWatcher watcher = new KillWatcher(condition, lock);
            zkClient.watch(getPath(SHAKA, SCHEDULE, agentIP, instanceId), watcher);
            zkClient.create(getPath(SHAKA, SCHEDULE, agentIP, DELETE, instanceId), new byte[0]);
            if (!condition.await(10, TimeUnit.SECONDS)) {
                throw new ExecuteException("Delete instance " + instanceId + " timeout.");
            }

            // validate the result of kill task
            runContext = (ExeContext) zkClient.getObject(path, null);
            if (runContext == null || runContext.getZkStatus() != ZKStatus.DELETED.code) {
                throw new ExecuteException("Delete instance " + instanceId
                                           + " error due to it's status is not deleted. ");
            }
        } catch (InterruptedException e) {
            throw new ExecuteException("Delete instance " + instanceId + " error.", e);
        } finally {
            String deletePath = getPath(SHAKA, SCHEDULE, agentIP, DELETE, instanceId);
            try {
                zkClient.removeWatcher(path);
                if (zkClient.exists(deletePath, null)) {
                    zkClient.remove(deletePath);
                }
            } catch (Exception e) {
                logger.error("Try to remove node error, path " + deletePath, e);
            }
            lock.unlock();
        }
    }
}
