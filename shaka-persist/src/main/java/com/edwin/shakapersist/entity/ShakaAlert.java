package com.edwin.shakapersist.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created by shichao.liao on 15/5/22.
 */
@Data
public class ShakaAlert {
    private int alertId;
    private int jobId;
    private String rules;
    private String alertUserIds;
    private int alertType;
    private String alertGroupIds;
    private Date addTime;
    private Date updateTime;

}
