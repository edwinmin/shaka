package com.edwin.shakacore.execute.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.edwin.shakacore.ShakaCoreContext;
import com.edwin.shakazookeeper.operator.ScheduleZKOperator;
import com.edwin.shakazookeeper.operator.ShakaScheduleZKOperator;

/**
 * @author jinming.wu
 * @date 2015-6-8
 */
public abstract class AbstractMonitor {

    protected final Logger       logger = LoggerFactory.getLogger(getClass());

    protected ScheduleZKOperator scheduleZKOperator;

    protected AbstractMonitor() {
        scheduleZKOperator = (ScheduleZKOperator) ShakaCoreContext.getInstance().getObjectByClazz(ShakaScheduleZKOperator.class);
    }

    /**
     * 重新链接
     */
    public abstract void reconnect();
}
