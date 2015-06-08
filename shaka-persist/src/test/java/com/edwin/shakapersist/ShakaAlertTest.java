package com.edwin.shakapersist;

import com.alibaba.fastjson.JSON;
import com.edwin.shakapersist.dao.ShakaAlertDao;
import com.edwin.shakapersist.entity.ShakaAlert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shichao.liao on 15/5/22.
 */
public class ShakaAlertTest extends AbstractTest{
    @Autowired
    ShakaAlertDao shakaAlertDao;

    @Test
    public void testAddShakaAlert(){
        ShakaAlert shakaAlert =new ShakaAlert();
        shakaAlert.setAlertGroupIds("34546");
        shakaAlert.setAlertType(2);
        shakaAlert.setRules("eggdf");
        shakaAlert.setAlertUserIds("242435,353354");
        shakaAlert.setJobId(2424);
        System.out.println(shakaAlertDao.addShakaAlert(shakaAlert));
        System.out.println(shakaAlert.getJobId());
    }

    @Test
    public void testLoadShakaAlert(){
        ShakaAlert shakaAlert =shakaAlertDao.loadShakaAlert(1);
        System.out.println(JSON.toJSONString(shakaAlert));
    }
}
