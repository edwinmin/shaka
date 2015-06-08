package com.edwin.shakacore.execute.policy;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentMap;

import com.edwin.shakapersist.entity.ShakaHost;
import com.edwin.shakapersist.entity.ShakaJob;
import com.google.common.collect.Maps;

/**
 * @author jinming.wu
 * @date 2015-6-2
 */
public class DefaultHostAssignPolicy extends HostAssignPolicy {

    private static ConcurrentMap<Integer, List<ShakaHost>> hostMap = Maps.newConcurrentMap();

    @Override
    public ShakaHost assignHost(ShakaJob job) {

        ShakaHost host = new ShakaHost();
        if (job.getHostGroupId() == 0) {
            host.setIP(job.getDeployServer());
            return host;
        }

        List<ShakaHost> hosts = hostMap.get(job.getHostGroupId());
        if (hosts == null || hosts.size() == 0) {
            hosts = hostDao.findHostsByGroup(job.getHostGroupId());
            if (hosts == null || hosts.size() == 0) {
                host = new ShakaHost();
                host.setIP(job.getDeployServer());
                return host;
            }
            List<ShakaHost> oldHosts = hostMap.putIfAbsent(job.getHostGroupId(), hosts);
            if (oldHosts != null && oldHosts != hosts) {
                hosts = oldHosts;
            }
        }

        return findHost(hosts);
    }

    private ShakaHost findHost(List<ShakaHost> hosts) {
        Random random = new Random();
        return hosts.get(random.nextInt(hosts.size()));
    }
}
