package com.edwin.shakaclient.component;

import com.edwin.shakaclient.entity.ExecuteResult;
import com.edwin.shakaclient.locator.ClientLocator;
import com.edwin.shakaclient.util.CommonExecHelper;
import com.edwin.shakazookeeper.exe.ExeContext;
import com.edwin.shakazookeeper.serializer.SerializableSerializer;
import com.edwin.shakazookeeper.serializer.ZkSerializer;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;

/**
 * Created by shichao.liao on 15/6/8.
 */
public class NewJobProcesor extends AbstractProcessor {

    private CuratorWatcher newWatcher;
    private String  agentIP;
    private static final String                             SCHEDULE = "schedule";

    private static final String                             NEW      = "new";

    private static final String                             DELETE   = "delete";

    private static final String                             RUN      = "run";

    private ThreadPoolTaskExecutor threadPoolTaskExecutor=ClientLocator.getexecuteThreadPools();

    NewJobProcesor(String agentIP) throws Exception {
        super();
        this.agentIP=agentIP;
    }


    @Override
    public void process() throws Exception {


        newWatcher = new CuratorWatcher() {
            @Override
            public void process(WatchedEvent event) throws Exception {
                if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    List<String> newInstanceList=zkClient.getChildren(getPath(SHAKA, SCHEDULE, agentIP, NEW), newWatcher);
                    executeJobs(newInstanceList);
                }
            }
        };

        List<String> newInstanceList=zkClient.getChildren(getPath(SHAKA, SCHEDULE, agentIP, NEW), newWatcher);
        executeJobs(newInstanceList);

    }

    private void executeJobs(List<String> newInstanceList) throws Exception{
        final ZkSerializer ZkSerializer=new SerializableSerializer();
        for(final String instanceId:newInstanceList){
            if(!zkClient.exists(getPath(SHAKA, SCHEDULE, agentIP, DELETE, instanceId))){
                threadPoolTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            byte[] instance = zkClient.getData(getPath(SHAKA, SCHEDULE, agentIP, instanceId));
                            ExeContext context = (ExeContext) ZkSerializer.deserialize(instance);
                            ExecuteResult result = CommonExecHelper.executeCommand(context.getCommand(), 0);
                            zkClient.remove(getPath(SHAKA, SCHEDULE, agentIP, instanceId));
                            zkClient.remove(getPath(SHAKA, SCHEDULE, agentIP, RUN, instanceId));
                        }catch (Exception e){

                        }
                    }
                });
                zkClient.create(getPath(SHAKA, SCHEDULE, agentIP, RUN, instanceId), new byte[0]);
                zkClient.remove(getPath(SHAKA, SCHEDULE, agentIP, NEW, instanceId));
            }

        }

    }
}
