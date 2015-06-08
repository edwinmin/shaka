package com.edwin.shakacore;

import org.junit.Test;
import org.quartz.SchedulerException;

/**
 * @author jinming.wu
 * @date 2015-5-29
 */
public class ShakaSchedulerTest extends BaseTest{
    
    ShakaScheduler shakaScheduler = new ShakaScheduler();
    
    @Test
    public void testStart() throws Exception{
        shakaScheduler.start();
    }
}
