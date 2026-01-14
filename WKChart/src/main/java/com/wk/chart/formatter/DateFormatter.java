package com.wk.chart.formatter;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间格式化工具类
 */
public class DateFormatter {
    private final ZoneId zoneId = ZoneId.systemDefault();//时区

    /**
     * 日期转换为制定格式字符串
     */
    public String formatDateToString(Date time, DateTimeFormatter pattern) {
        try {
            return time.toInstant().atZone(zoneId).format(pattern);
        } catch (Exception e) {
            return "";
        }
    }
}

