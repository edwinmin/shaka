package com.edwin.shakapersist.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created by shichao.liao on 15/5/21.
 */
@Data
public class ShakaJob {

    private int    jobId;
    private String name;
    private String description;
    private int    type;
    private String deployServer;
    private String schedulerServer;
    private int    hostGroupId;
    private int    owner;
    private int    creator;
    private String exeIdentity;
    private int    status;
    private String dependencyExpr;
    private String corn;
    private String command;
    private int    exeTimeOut;
    private int    waitTimeOut;
    private int    retryTimes;
    private int    autoKill;
    private Date   addTime;
    private Date   updateTime;
}
