package com.edwin.shakautils.date;

import java.util.Date;

import org.joda.time.DateTime;

/**
 * @author jinming.wu
 * @date 2015-6-1
 */
public class DateHelper {

    public static Date getDefaultTime() {
        return DateTime.parse("1971-01-01").toDate();
    }
}
