package com.edwin.shakazookeeper.exe;

import java.util.Date;

import org.joda.time.DateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * 时间包装（为了清除内存垃圾）
 * 
 * @author jinming.wu
 * @date 2015-6-3
 */
public class DateWrapper<T> {

    @Setter
    @Getter
    private Date date;

    @Setter
    private T    t;

    public DateWrapper(T t) {
        date = DateTime.now().toDate();
        this.t = t;
    }

    public T getObject() {
        return t;
    }
}
