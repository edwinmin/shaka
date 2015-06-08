package com.edwin.shakapersist.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created by shichao.liao on 15/5/22.
 */
@Data
public class ShakaTask {
    private String instanceId;
    private String taskId;
    private int jobId;
    private Date startTime;
    private Date endTime;
    private Date scheduleTime;
    private int status;
    private int returnCode;
    private String exeHostIP;
    private int logId;
    private Date addTime;
    private Date updateTime;
}
