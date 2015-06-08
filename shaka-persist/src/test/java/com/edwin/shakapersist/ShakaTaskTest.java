package com.edwin.shakapersist;

import com.alibaba.fastjson.JSON;
import com.edwin.shakapersist.dao.ShakaTaskDao;
import com.edwin.shakapersist.entity.ShakaTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * Created by shichao.liao on 15/5/22.
 */
public class ShakaTaskTest extends AbstractTest{
    @Autowired
    ShakaTaskDao shakaTaskDao;

    @Test
    public void testAddShakaTask(){
        ShakaTask shakaTask =new ShakaTask();
        shakaTask.setInstanceId("20150522192.168.0.1");
        shakaTask.setTaskId("123");
        shakaTask.setStatus(1);
        shakaTask.setJobId(12);
        shakaTask.setEndTime(new Date());
        shakaTask.setExeHostIP("192.168.0.1");
        shakaTask.setTaskId("201453");
        shakaTask.setLogId(3234);
        shakaTask.setReturnCode(1);
        shakaTask.setStartTime(new Date());
        shakaTask.setScheduleTime(new Date());
        System.out.println(shakaTaskDao.addShakaTask(shakaTask));
    }

    @Test
    public void testLoadShakaTask(){
        ShakaTask shakaTask =shakaTaskDao.loadShakaTask("20150522192.168.0.1");
        System.out.println(JSON.toJSONString(shakaTask));
    }
}
