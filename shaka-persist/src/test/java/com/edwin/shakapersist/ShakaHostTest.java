package com.edwin.shakapersist;

import com.alibaba.fastjson.JSON;
import com.edwin.shakapersist.dao.ShakaHostDao;
import com.edwin.shakapersist.entity.ShakaHost;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        ShakaHost shakaHost =shakaHostDao.loadShakaHost("192.168.0.1");
        System.out.println(JSON.toJSONString(shakaHost));
    }
    
    @Test
    public void testAddOrUpdate(){
        ShakaHost shakaHost = new ShakaHost();
        shakaHost.setIP("192.168.0iii.1");
        shakaHost.setOnline(1);
        shakaHostDao.addOrUpdate(shakaHost);
    }
}
