package com.edwin.shakacore.spring;

import com.edwin.shakapersist.dao.ShakaHostDao;
import com.edwin.shakapersist.dao.ShakaJobDao;
import com.edwin.shakapersist.dao.ShakaTaskDao;

/**
 * @author jinming.wu
 * @date 2015-5-26
 */
public class RepositoryLocator extends BeanLocator {

    public static ShakaJobDao getShakaJobDao() {
        return getBean("shakaJobDao");
    }
    
    public static ShakaTaskDao getShakaTaskDao() {
        return getBean("shakaTaskDao");
    }
    
    public static ShakaHostDao getShakaHostDao() {
        return getBean("shakaHostDao");
    }
}
