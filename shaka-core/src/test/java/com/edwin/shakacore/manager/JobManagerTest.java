package com.edwin.shakacore.manager;

import org.junit.Test;

import com.edwin.shakacore.BaseTest;

/**
 * @author jinming.wu
 * @date 2015-5-27
 */
public class JobManagerTest extends BaseTest{

    @Test
    public void testLoadScheduleJobs() {
        JobManager jobManager = new ShakaJobManager();
        System.out.println(jobManager.loadScheduleJobs());
    }
}
