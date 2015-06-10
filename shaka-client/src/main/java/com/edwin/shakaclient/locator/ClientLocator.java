package com.edwin.shakaclient.locator;

import com.edwin.shakapersist.dao.ShakaJobDao;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by shichao.liao on 15/6/9.
 */
public class ClientLocator extends BeanLocator{

    public static ShakaJobDao getShakaJobDao(){
        return getBean("shakaJobDao");
    }

    public static ThreadPoolTaskExecutor getexecuteThreadPools() {
        return getBean("executeThreadPool");
    }

}
