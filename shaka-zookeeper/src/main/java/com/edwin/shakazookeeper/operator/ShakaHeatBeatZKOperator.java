package com.edwin.shakazookeeper.operator;

import com.edwin.shakautils.lang.ByteHelper;
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

    public ShakaHeatBeatZKOperator() {
        super();
    }

    public ShakaHeatBeatZKOperator(ZKClient zkClient) {
        super(zkClient);
    }

    @Override
    public void register(MachineType machineType, String machineIP) throws Exception {

        init();

        if (zkClient.exists(getPath(SHAKA, HEARTBEAT, machineType.toString().toLowerCase(), REALTIME, machineIP), false)) {
            zkClient.remove(getPath(SHAKA, HEARTBEAT, machineType.toString().toLowerCase(), REALTIME, machineIP));
        }

        zkClient.create(getPath(SHAKA, HEARTBEAT, machineType.toString().toLowerCase(), REALTIME, machineIP),
                        new byte[0]);
        zkClient.create(getPath(SHAKA, HEARTBEAT, machineType.toString().toLowerCase(), INFO, machineIP), new byte[0]);
    }

    private void init() throws Exception {

        if (!zkClient.exists(getPath(SHAKA, HEARTBEAT), false)) {
            zkClient.create(getPath(SHAKA, HEARTBEAT, MachineType.SERVER.toString().toLowerCase(), REALTIME),
                            new byte[0]);
            zkClient.create(getPath(SHAKA, HEARTBEAT, MachineType.SERVER.toString().toLowerCase(), INFO), new byte[0]);
            zkClient.create(getPath(SHAKA, HEARTBEAT, MachineType.AGENT.toString().toLowerCase(), REALTIME),
                            new byte[0]);
            zkClient.create(getPath(SHAKA, HEARTBEAT, MachineType.AGENT.toString().toLowerCase(), INFO), new byte[0]);
        }
    }

    @Override
    public void updateHeartBeat(MachineType machineType, String machineIP) throws Exception {

        String path = getPath(SHAKA, HEARTBEAT, machineType.toString().toLowerCase(), REALTIME, machineIP);
        if (zkClient.exists(path, false)) {
            zkClient.update(path, null, ByteHelper.getLongBytes(System.currentTimeMillis()));
        }
    }
}
