package com.edwin.shakazookeeper.client;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.framework.api.CuratorEventType;
import org.apache.curator.framework.api.CuratorListener;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.shakazookeeper.listener.ZKDataListener;
import com.google.common.collect.Maps;

/**
 * 事件监听器（一旦有事件产生就会有触发，可以监听所有事件包括watch事件，watch事件只是curator事件的一种）
 * 
 * @author jinming.wu
 * @date 2015-5-22
 */
public class ShakaCuratorListener implements CuratorListener {

    private static final Logger                         logger          = LoggerFactory.getLogger(ShakaCuratorListener.class);

    private ConcurrentMap<String, List<ZKDataListener>> dataListenerMap = Maps.newConcurrentMap();

    private ZKClient                                    zkClient;

    public ShakaCuratorListener(ZKClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void eventReceived(CuratorFramework client, CuratorEvent event) throws Exception {

        if (event == null) {
            return;
        }

        if (event.getType() == CuratorEventType.CLOSING) {
            logger.error("Zookeeper is closing. ");
        }
        if (event.getType() == CuratorEventType.WATCHED) {
            if (event.getWatchedEvent().getPath() != null) {
                processEvent(event.getWatchedEvent());
            }
        }
    }

    private void processEvent(WatchedEvent event) {

        if (event.getType() == EventType.NodeCreated || event.getType() == EventType.NodeDataChanged) {
        } else if (event.getType() == EventType.NodeDeleted) {
        } else if (event.getType() == EventType.NodeChildrenChanged) {
        }
    }
}
