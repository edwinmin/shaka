package com.edwin.shakacore.id;

import java.text.NumberFormat;

import lombok.Getter;

import com.edwin.shakacore.ShakaCoreContext;

/**
 * @author jinming.wu
 * @date 2015-5-25
 */
public abstract class ID {

    protected static final NumberFormat format    = NumberFormat.getInstance();

    protected static final String       SEPARATOR = "_";

    protected String                    hostIP;

    /** 作业ID */
    protected int                       jobId;

    /** 全局递增号 */
    @Getter
    protected int                       inc;

    static {
        format.setGroupingUsed(false);
        format.setMinimumIntegerDigits(4);
    }

    public ID(int inc) {
        this.inc = inc;
        this.hostIP = ShakaCoreContext.getInstance().getHostIP();
    }
}
