package com.edwin.shakapersist.entity;

import lombok.Data;

import java.util.Date;

/**
 * Created by shichao.liao on 15/5/22.
 */
@Data
public class ShakaHost {

    private int    id;
    private String iP;
    private String name;
    private int    online;
    private int    groupId;
    private Date   addTime;
    private Date   updateTime;
}
