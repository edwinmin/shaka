package com.edwin.shakapersist;

import com.alibaba.fastjson.JSON;
import com.edwin.shakapersist.dao.ShakaJobDao;
import com.edwin.shakapersist.entity.ShakaJob;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shichao.liao on 15/5/21.
 */
public class ShakaJobTest extends AbstractTest{
    @Autowired
    ShakaJobDao shakaJobDao;

    @Test
    public void testAddShakaJob(){
        ShakaJob jobConfig =new ShakaJob();
        jobConfig.setName("dsfvfvd");
        jobConfig.setStatus(3);
        jobConfig.setCommand("java -jar sddgdfg.jar");
        jobConfig.setCorn("3456");
        jobConfig.setDescription("dsfvfdv");
        jobConfig.setExeTimeOut(5000);
        jobConfig.setOwner(111);
        jobConfig.setDeployServer("10.1.1.1");
        jobConfig.setSchedulerServer("1.2.3.4");
        jobConfig.setExeIdentity("efrr");
        jobConfig.setDependencyExpr("");
        System.out.println(shakaJobDao.addShakaJob(jobConfig));
        System.out.println(jobConfig.getJobId());
    }

    @Test
    public void testLoadShakaJob(){
        ShakaJob jobConfigNew =shakaJobDao.loadShakaJob(1);
        System.out.println(JSON.toJSONString(jobConfigNew));
    }

}
