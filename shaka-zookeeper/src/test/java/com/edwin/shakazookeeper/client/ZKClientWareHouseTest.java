package com.edwin.shakazookeeper.client;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.edwin.shakautils.IPHelper;
import com.edwin.shakazookeeper.BaseTest;
import com.edwin.shakazookeeper.Environment;
import com.edwin.shakazookeeper.MachineType;
import com.edwin.shakazookeeper.ShakaZKContext;
import com.edwin.shakazookeeper.operator.ShakaHeatBeatZKOperator;
import com.edwin.shakazookeeper.operator.ZKOperator;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public class ZKClientWareHouseTest extends BaseTest {

    private ZKClientWareHouse zkClientWareHouse  = new ZKClientWareHouse();

    @Test
    public void testGetZKClient() throws Exception {

        Environment env = ShakaZKContext.getInstance().getEnv();
        ZKClient zkClient = zkClientWareHouse.getZKClient(env);
        ZKOperator zkOperator = new ShakaHeatBeatZKOperator(zkClient);
        //zkOperator.register(MachineType.AGENT, IPHelper.getHostIP());
        //zkOperator.updateHeartBeat(MachineType.AGENT, IPHelper.getHostIP());
        zkOperator.register(MachineType.SERVER, IPHelper.getHostIP());
        Thread.sleep(1000000);
    }

    @Test
    public void testRemove() throws Exception {
        Environment env = ShakaZKContext.getInstance().getEnv();
        ZKClient zkClient = zkClientWareHouse.getZKClient(env);
        zkClient.remove("/shaka");
        Thread.sleep(1000000);
    }
}
