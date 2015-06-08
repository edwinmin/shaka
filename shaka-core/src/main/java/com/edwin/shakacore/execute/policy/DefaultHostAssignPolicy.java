package com.edwin.shakacore.execute.policy;

import java.util.List;
import java.util.Random;

import com.edwin.shakapersist.entity.ShakaHost;
import com.edwin.shakapersist.entity.ShakaJob;

/**
 * 实时从数据库中读取机器来分配
 * 
 * @author jinming.wu
 * @date 2015-6-2
 */
public class DefaultHostAssignPolicy extends HostAssignPolicy {

    @Override
    public ShakaHost assignHost(ShakaJob job) {

        ShakaHost host = new ShakaHost();
        if (job.getHostGroupId() == 0) {

            host = hostDao.loadShakaHost(job.getDeployServer());
            if (host == null || host.getOnline() == 1) {
                return null;
            }
            return host;
        }

        List<ShakaHost> hosts = hostDao.findHostsByGroup(job.getHostGroupId());
        if (hosts == null || hosts.size() == 0) {
            host = hostDao.loadShakaHost(job.getDeployServer());
            if (host == null || host.getOnline() == 1) {
                return null;
            }
            return host;
        }

        return findHost(hosts);
    }

    private ShakaHost findHost(List<ShakaHost> hosts) {
        Random random = new Random();
        return hosts.get(random.nextInt(hosts.size()));
    }
}
