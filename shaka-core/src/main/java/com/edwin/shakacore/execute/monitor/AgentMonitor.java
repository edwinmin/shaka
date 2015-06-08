package com.edwin.shakacore.execute.monitor;

import java.util.List;

import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;

import com.edwin.shakacore.spring.RepositoryLocator;
import com.edwin.shakapersist.dao.ShakaHostDao;
import com.edwin.shakapersist.entity.ShakaHost;
import com.edwin.shakazookeeper.MachineType;

/**
 * @author jinming.wu
 * @date 2015-6-8
 */
public class AgentMonitor extends AbstractMonitor {

    private List<String>   agentsIPs;

    private CuratorWatcher watcher;

    private ShakaHostDao   shakaHostDao = RepositoryLocator.getShakaHostDao();

    public AgentMonitor() {
        super();
    }

    public void monitor() {

        watcher = new CuratorWatcher() {

            @Override
            public void process(WatchedEvent event) throws Exception {
                if (event.getType() == EventType.NodeChildrenChanged) {
                    List<String> newAgentIPs = scheduleZKOperator.getChildHBNodes(MachineType.AGENT, watcher);
                    for (String agentIP : agentsIPs) {
                        ShakaHost host = new ShakaHost();
                        host.setIP(agentIP);
                        if (newAgentIPs.contains(agentIP)) {
                            host.setOnline(0);
                        } else {
                            host.setOnline(1);
                        }
                        shakaHostDao.addOrUpdate(host);
                    }

                    for (String agentIP : newAgentIPs) {
                        ShakaHost host = new ShakaHost();
                        host.setIP(agentIP);
                        host.setOnline(0);
                        shakaHostDao.addOrUpdate(host);
                    }
                    agentsIPs = newAgentIPs;
                }
            }
        };

        try {
            agentsIPs = scheduleZKOperator.getChildHBNodes(MachineType.AGENT, watcher);
        } catch (Exception e) {
            logger.error("Find agent ips error. ", e);
            return;
        }

        for (String ip : agentsIPs) {
            ShakaHost host = new ShakaHost();
            host.setIP(ip);
            host.setOnline(0);
            shakaHostDao.addOrUpdate(host);
        }
    }

    @Override
    public void reconnect() {

        try {
            scheduleZKOperator.getChildHBNodes(MachineType.AGENT, watcher);
        } catch (Exception e) {
            logger.error("Find agent ips error. ", e);
        }
    }
}
