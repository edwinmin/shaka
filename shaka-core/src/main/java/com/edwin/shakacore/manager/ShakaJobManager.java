package com.edwin.shakacore.manager;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.shakacore.ShakaCoreContext;
import com.edwin.shakacore.exception.ShakaException;
import com.edwin.shakacore.exception.ShakaNotFoundException;
import com.edwin.shakacore.execute.JobStatus;
import com.edwin.shakacore.quartz.QuartzShakaJob;
import com.edwin.shakacore.quartz.QuartzShakaJobBuilder;
import com.edwin.shakacore.spring.RepositoryLocator;
import com.edwin.shakapersist.dao.ShakaJobDao;
import com.edwin.shakapersist.entity.ShakaJob;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author jinming.wu
 * @date 2015-5-27
 */
public class ShakaJobManager implements JobManager {

    private static final Logger          logger      = LoggerFactory.getLogger(ShakaJobManager.class);

    // private static final String JOB = "Job";

    private static final String          TRIGGER     = "Trigger";

    private static Lock                  lock        = new ReentrantLock();

    private ShakaCoreContext             shakaCoreContext;

    private Map<Integer, QuartzShakaJob> qtzShakaJobMap;

    private Scheduler                    scheduler;

    private ShakaJobDao                  shakaJobDao = RepositoryLocator.getShakaJobDao();

    // private ShakaTaskDao shakaTaskDao;

    public ShakaJobManager() {
        this.shakaCoreContext = ShakaCoreContext.getInstance();
        this.qtzShakaJobMap = shakaCoreContext.getQtzShakaJobMap();
        this.scheduler = shakaCoreContext.getScheduler();
    }

    @Override
    public Map<Integer, ShakaJob> loadScheduleJobs() {

        String hostIP = shakaCoreContext.getHostIP();
        List<Integer> statusList = Arrays.asList(JobStatus.SCHEDULING.status, JobStatus.SUSPEND.status);
        List<ShakaJob> jobs = shakaJobDao.findShakaJobsByStatus(statusList, hostIP);
        if (jobs != null && !jobs.isEmpty()) {
            Map<Integer, ShakaJob> templateMap = Maps.newConcurrentMap();
            for (ShakaJob job : jobs) {
                templateMap.put(job.getJobId(), job);
            }
            return templateMap;
        }
        return Collections.emptyMap();
    }

    @Override
    public void initJobs() {

        Map<Integer, ShakaJob> shakaJobsFromDB = this.loadScheduleJobs();
        if (shakaJobsFromDB != null) {
            for (Entry<Integer, ShakaJob> entry : shakaJobsFromDB.entrySet()) {
                try {
                    QuartzShakaJob quartzShakaJob = qtzShakaJobMap.get(entry.getValue().getJobId());
                    if (quartzShakaJob == null) {
                        quartzShakaJob = QuartzShakaJobBuilder.newBuilder(entry.getValue()).withJobClazz(null).withJobDetail().withCronTrigger().build();
                        qtzShakaJobMap.put(entry.getKey(), quartzShakaJob);
                    }

                    scheduler.scheduleJob(quartzShakaJob.getJobDetail(), quartzShakaJob.getCronTrigger());
                    if (quartzShakaJob.getShakaJob().getStatus() == JobStatus.SUSPEND.status) {
                        scheduler.pauseTrigger(quartzShakaJob.getCronTrigger().getName(), Scheduler.DEFAULT_GROUP);
                        scheduler.pauseJob(quartzShakaJob.getJobDetail().getName(), Scheduler.DEFAULT_GROUP);
                    }
                } catch (Exception e) {
                    logger.error("Generate class error. JobId " + entry.getValue().getJobId(), e);
                }
            }
        }
    }

    @Override
    public void addJob(ShakaJob job) throws Exception {

        int jobId = shakaJobDao.addShakaJob(job);
        job.setJobId(jobId);

        scheduleNewJob(job);
    }

    @Override
    public void updateJob(ShakaJob job) throws Exception {

        lock.lock();

        try {

            QuartzShakaJob qtzShakaJob = qtzShakaJobMap.get(job.getJobId());
            if (qtzShakaJob == null || qtzShakaJob.getShakaJob() == null
                || qtzShakaJob.getShakaJob().getStatus() == JobStatus.DELETED.status) {
                throw new ShakaNotFoundException("Job " + job.getJobId() + " has not been found. ");
            }

            ShakaJob oldJob = qtzShakaJob.getShakaJob();
            if (job.getStatus() != oldJob.getStatus()) {
                throw new ShakaNotFoundException("Job " + job.getJobId()
                                                 + " can not be updated to other status through this method. ");
            }

            shakaJobDao.updateShakaJob(job);

            qtzShakaJob.setShakaJob(job);
            if (job.getStatus() == JobStatus.SUSPEND.status) {
                return;
            }

            CronTrigger cronTrigger = new CronTrigger("Trigger" + job.getJobId(), Scheduler.DEFAULT_GROUP);
            cronTrigger.setCronExpression(job.getCorn());
            cronTrigger.setJobName(qtzShakaJob.getJobDetail().getName());
            qtzShakaJob.setCronTrigger(cronTrigger);

            scheduler.rescheduleJob(cronTrigger.getName(), Scheduler.DEFAULT_GROUP, cronTrigger);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean removeJob(int jobId) throws SchedulerException {

        lock.lock();

        try {

            QuartzShakaJob qtzShakaJob = qtzShakaJobMap.get(jobId);
            if (qtzShakaJob == null || qtzShakaJob.getShakaJob() == null
                || qtzShakaJob.getShakaJob().getStatus() == JobStatus.DELETED.status) {
                throw new ShakaNotFoundException("Job " + jobId + " has not been found. ");
            }

            // ensure task is not running
            // ...

            shakaJobDao.updateJobStatus(jobId, JobStatus.DELETED.status);

            scheduler.pauseTrigger(qtzShakaJob.getCronTrigger().getName(), Scheduler.DEFAULT_GROUP);
            scheduler.pauseJob(qtzShakaJob.getJobDetail().getName(), Scheduler.DEFAULT_GROUP);
            scheduler.deleteJob(qtzShakaJob.getJobDetail().getName(), Scheduler.DEFAULT_GROUP);

            qtzShakaJob.getShakaJob().setStatus(JobStatus.DELETED.status);
            qtzShakaJobMap.remove(jobId);

            return scheduler.unscheduleJob(qtzShakaJob.getCronTrigger().getName(), Scheduler.DEFAULT_GROUP);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void manualRun(int jobId) throws Exception {

    }

    @Override
    public void suspend(int jobId) throws Exception {

        lock.lock();

        try {

            QuartzShakaJob qtzShakaJob = qtzShakaJobMap.get(jobId);
            if (qtzShakaJob == null || qtzShakaJob.getShakaJob() == null
                || qtzShakaJob.getShakaJob().getStatus() == JobStatus.DELETED.status) {
                throw new ShakaNotFoundException("Job " + jobId + " has not been found. ");
            }

            if (qtzShakaJob.getShakaJob().getStatus() == JobStatus.SUSPEND.status) {
                throw new ShakaException("Job " + jobId + " is suspend. ");
            }

            shakaJobDao.updateJobStatus(jobId, JobStatus.SUSPEND.status);

            scheduler.pauseTrigger(qtzShakaJob.getCronTrigger().getName(), Scheduler.DEFAULT_GROUP);
            scheduler.pauseJob(qtzShakaJob.getJobDetail().getName(), Scheduler.DEFAULT_GROUP);

            qtzShakaJob.getShakaJob().setStatus(JobStatus.SUSPEND.status);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void resume(int jobId) throws Exception {

        lock.lock();

        try {

            QuartzShakaJob qtzShakaJob = qtzShakaJobMap.get(jobId);
            if (qtzShakaJob == null || qtzShakaJob.getShakaJob() == null
                || qtzShakaJob.getShakaJob().getStatus() == JobStatus.DELETED.status) {
                throw new ShakaNotFoundException("Job " + jobId + " has not been found. ");
            }

            if (qtzShakaJob.getShakaJob().getStatus() == JobStatus.SCHEDULING.status) {
                throw new ShakaException("Job " + jobId + " is scheduling. ");
            }

            shakaJobDao.updateJobStatus(jobId, JobStatus.SCHEDULING.status);

            CronTrigger cronTrigger = new CronTrigger(TRIGGER + jobId, Scheduler.DEFAULT_GROUP);
            cronTrigger.setCronExpression(qtzShakaJob.getShakaJob().getCorn());
            cronTrigger.setJobName(qtzShakaJob.getJobDetail().getName());
            qtzShakaJob.setCronTrigger(cronTrigger);

            scheduler.rescheduleJob(cronTrigger.getName(), Scheduler.DEFAULT_GROUP, cronTrigger);

            qtzShakaJob.getShakaJob().setStatus(JobStatus.SCHEDULING.status);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void activate(int jobId) throws Exception {

        QuartzShakaJob qtzShakaJob = qtzShakaJobMap.get(jobId);
        if (qtzShakaJob == null) {
            ShakaJob shakaJob = shakaJobDao.loadShakaJob(jobId);
            if (shakaJob == null) {
                throw new ShakaNotFoundException("Job " + jobId + " has not been found. ");
            }
            scheduleNewJob(shakaJob);
        } else {
            qtzShakaJob.getShakaJob().setStatus(JobStatus.SCHEDULING.status);
            newTrigger(qtzShakaJob);
            scheduler.scheduleJob(qtzShakaJob.getJobDetail(), qtzShakaJob.getCronTrigger());
        }
    }

    @Override
    public void refresh() throws Exception {

        lock.lock();

        try {

            Map<Integer, ShakaJob> shakaJobsFromDB = this.loadScheduleJobs();
            if (shakaJobsFromDB == null) {
                shakaJobsFromDB = Maps.newHashMap();
            }

            Set<Integer> jobIdsInMem = Sets.newHashSet(qtzShakaJobMap.keySet());
            jobIdsInMem.removeAll(shakaJobsFromDB.keySet());
            stopSchedule(jobIdsInMem);

            for (Entry<Integer, ShakaJob> entry : shakaJobsFromDB.entrySet()) {
                ShakaJob jobFromDB = entry.getValue();
                QuartzShakaJob qtzJobInMem = qtzShakaJobMap.get(entry.getKey());
                if (qtzJobInMem != null) {
                    ShakaJob jobInMem = qtzJobInMem.getShakaJob();
                    if (jobInMem.getStatus() == jobFromDB.getStatus()) {
                        continue;
                    }

                    if (jobInMem.getStatus() == JobStatus.DELETED.status) {
                        qtzJobInMem.setShakaJob(jobFromDB);
                        newTrigger(qtzJobInMem);
                        if (jobFromDB.getStatus() == JobStatus.SCHEDULING.status) {
                            scheduler.scheduleJob(qtzJobInMem.getJobDetail(), qtzJobInMem.getCronTrigger());
                        }
                        continue;
                    }

                    if (jobFromDB.getStatus() == JobStatus.SUSPEND.status) {
                        this.suspend(entry.getKey());
                    } else {
                        this.resume(entry.getKey());
                    }
                    qtzJobInMem.setShakaJob(jobFromDB);
                } else {
                    scheduleNewJob(jobFromDB);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void stopSchedule(Set<Integer> jobIds) throws Exception {

        for (Integer jobId : jobIds) {

            QuartzShakaJob qtzShakaJob = qtzShakaJobMap.get(jobId);

            // ensure task not running
            // ...

            scheduler.pauseTrigger(qtzShakaJob.getCronTrigger().getName(), Scheduler.DEFAULT_GROUP);
            scheduler.pauseJob(qtzShakaJob.getJobDetail().getName(), Scheduler.DEFAULT_GROUP);
            scheduler.deleteJob(qtzShakaJob.getJobDetail().getName(), Scheduler.DEFAULT_GROUP);
            qtzShakaJobMap.remove(jobId);
        }
    }

    private void newTrigger(QuartzShakaJob qtzJobInMem) throws ParseException {

        CronTrigger cronTrigger = new CronTrigger(TRIGGER + qtzJobInMem.getShakaJob().getJobId(),
                                                  Scheduler.DEFAULT_GROUP, qtzJobInMem.getShakaJob().getCorn());
        cronTrigger.setJobName(qtzJobInMem.getJobDetail().getName());
        qtzJobInMem.setCronTrigger(cronTrigger);
    }

    private void scheduleNewJob(ShakaJob job) throws Exception {

        QuartzShakaJob newQtzShakaJob = QuartzShakaJobBuilder.newBuilder(job).withJobClazz(null).withJobDetail().withCronTrigger().build();

        qtzShakaJobMap.put(job.getJobId(), newQtzShakaJob);

        if (job.getStatus() == JobStatus.SCHEDULING.status) {
            scheduler.scheduleJob(newQtzShakaJob.getJobDetail(), newQtzShakaJob.getCronTrigger());
        }
    }
}
