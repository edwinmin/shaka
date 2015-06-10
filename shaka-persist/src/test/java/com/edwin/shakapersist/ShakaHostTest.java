package com.edwin.shakapersist;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.edwin.shakapersist.dao.ShakaHostDao;
import com.edwin.shakapersist.entity.ShakaHost;

/**
 * Created by shichao.liao on 15/5/22.
 */
public class ShakaHostTest extends AbstractTest{
    @Autowired
    ShakaHostDao shakaHostDao;

    @Test
    public void testAddShakaHost(){

        ShakaHost shakaHost=new ShakaHost();
        shakaHost.setIP("192.168.0.1");
        shakaHost.setName("shaka-host");
        shakaHost.setOnline(1);
        System.out.println(shakaHostDao.addShakaHost(shakaHost));
        System.out.println(shakaHost.getId());
    }

    @Test
    public void testLoadShakaHost(){
        shakaHostDao.loadShakaHost("tests");
    }
    
    @Test
    public void test(){
        ShakaHost shakaHost = new ShakaHost();
        shakaHost.setIP("192.168.0iii.1");
        shakaHost.setOnline(1);
        shakaHostDao.addOrUpdate(shakaHost);
    }
}
