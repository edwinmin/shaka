package com.edwin.shakazookeeper.operator;

import java.util.List;

import org.apache.curator.framework.api.CuratorWatcher;

import com.edwin.shakazookeeper.MachineType;
import com.edwin.shakazookeeper.client.ZKClient;

/**
 * @author jinming.wu
 * @date 2015-5-22
 */
public class ShakaHeatBeatZKOperator extends AbstractShakaZKOperator {

    private static final String HEARTBEAT = "heartbeat";

    private static final String REALTIME  = "realtime";

    private static final String INFO      = "info";

    public ShakaHeatBeatZKOperator() throws Exception {
        super();
    }

    public ShakaHeatBeatZKOperator(ZKClient zkClient) {
        super(zkClient);
    }

    @Override
    public void register(MachineType machineType, String machineIP) throws Exception {

        init();

        // 创建临时心跳节点
        zkClient.createTemp(getPath(SHAKA, HEARTBEAT, machineType.toString().toLowerCase(), REALTIME, machineIP));

        zkClient.createTemp(getPath(SHAKA, HEARTBEAT, machineType.toString().toLowerCase(), INFO, machineIP));
    }

    private void init() throws Exception {

        if (!zkClient.exists(getPath(SHAKA, HEARTBEAT), null)) {
            zkClient.create(getPath(SHAKA, HEARTBEAT, MachineType.SERVER.toString().toLowerCase(), REALTIME),
                            new byte[0]);
            zkClient.create(getPath(SHAKA, HEARTBEAT, MachineType.SERVER.toString().toLowerCase(), INFO), new byte[0]);
            zkClient.create(getPath(SHAKA, HEARTBEAT, MachineType.AGENT.toString().toLowerCase(), REALTIME),
                            new byte[0]);
            zkClient.create(getPath(SHAKA, HEARTBEAT, MachineType.AGENT.toString().toLowerCase(), INFO), new byte[0]);
        }
    }

    @Override
    public List<String> getChildHBNodes(MachineType machineType,CuratorWatcher watcher) throws Exception {
        return zkClient.getChildren(getPath(SHAKA, HEARTBEAT, machineType.toString().toLowerCase(), REALTIME), watcher);
    }
}
