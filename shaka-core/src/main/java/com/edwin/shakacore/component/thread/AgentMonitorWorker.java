package com.edwin.shakacore.component.thread;

import java.util.concurrent.atomic.AtomicBoolean;

import com.edwin.shakacore.ShakaCoreContext;
import com.edwin.shakacore.execute.monitor.AbstractMonitor;
import com.edwin.shakacore.execute.monitor.AgentMonitor;

/**
 * 监控agent线程，防止wtcher丢失
 * 
 * @author jinming.wu
 * @date 2015-6-8
 */
public class AgentMonitorWorker extends AbstractWorker {

    private AbstractMonitor agentMonitor;

    private AtomicBoolean   isInterrupted;

    public AgentMonitorWorker() {
        isInterrupted = new AtomicBoolean(false);
        agentMonitor = new AgentMonitor();
    }

    @Override
    public void run() {
        while (!isInterrupted.get()) {
            agentMonitor.reconnect();
            try {
                Thread.sleep(5 * 60 * 1000);
            } catch (InterruptedException e) {
                logger.error("Refresh thread is interrupted. ", e);
            }
        }
    }

    @Override
    protected void shutdown() {
        isInterrupted.compareAndSet(false, true);
    }

    @Override
    protected void setShakaCoreContext(ShakaCoreContext shakaCoreContext) {
    }
}
