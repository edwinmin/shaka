package com.edwin.shakacore.component.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.shakacore.ShakaCoreContext;

/**
 * @author jinming.wu
 * @date 2015-5-28
 */
public abstract class AbstractWorker implements Runnable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 关闭守护进程
     */
    protected abstract void shutdown();

    /**
     * 设置调度上下文
     * 
     * @param shakaCoreContext
     */
    protected abstract void setShakaCoreContext(ShakaCoreContext shakaCoreContext);
}
