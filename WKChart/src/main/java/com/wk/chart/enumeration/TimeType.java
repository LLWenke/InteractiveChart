package com.wk.chart.enumeration;


import androidx.annotation.Nullable;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

/**
 * <p>TimeType</p>
 * 图表时间类型
 */

public enum TimeType implements Serializable {
    ONE_MINUTE(0, DateTimeFormatter.ofPattern("HH:mm"), 1, 60000, "1min"),//一分钟

    FIVE_MINUTE(1, ONE_MINUTE.pattern, 5, ONE_MINUTE.msec, "5min"),//五分钟

    FIFTEEN_MINUTE(2, ONE_MINUTE.pattern, 15, ONE_MINUTE.msec, "15min"),//十五分钟

    THIRTY_MINUTE(3, ONE_MINUTE.pattern, 30, ONE_MINUTE.msec, "30min"),//三十分钟

    ONE_HOUR(4, ONE_MINUTE.pattern, 1, ONE_MINUTE.msec * 60, "60min"),//一小时

    TWO_HOUR(5, DateTimeFormatter.ofPattern("MM-dd HH:mm"), 2, ONE_HOUR.msec, "2hour"),//二小时

    THREE_HOUR(6, TWO_HOUR.pattern, 3, ONE_HOUR.msec, "3hour"),//三小时

    FOUR_HOUR(7, TWO_HOUR.pattern, 4, ONE_HOUR.msec, "4hour"),//四小时

    SIX_HOUR(8, TWO_HOUR.pattern, 6, ONE_HOUR.msec, "6hour"),//六小时

    EIGHT_HOUR(9, TWO_HOUR.pattern, 8, ONE_HOUR.msec, "8hour"),//八小时

    TWELVE_HOUR(10, TWO_HOUR.pattern, 12, ONE_HOUR.msec, "12hour"),//十二小时

    DAY(11, DateTimeFormatter.ofPattern("MM-dd"), 1, ONE_HOUR.msec * 24, "1day"),//天

    WEEK(12, DateTimeFormatter.ofPattern("yy-MM-dd"), 7, DAY.msec, "1week"),//周

    MONTH(13, DateTimeFormatter.ofPattern("yy-MM-dd HH:mm"), 1, 0, "1month");//月

    TimeType(int nativeInt, DateTimeFormatter pattern, int value, long msec, String param) {
        this.pattern = pattern;
        this.value = value;
        this.msec = msec;
        this.nativeInt = nativeInt;
        this.param = param;
    }

    private final DateTimeFormatter pattern;
    private final int value;
    private final long msec;
    private final int nativeInt;
    private final String param;
    private static final int HASHCODE = TimeType.class.hashCode();

    public final DateTimeFormatter pattern() {
        return pattern;
    }

    public final int value() {
        return value;
    }

    public final long msec() {
        return msec;
    }

    public final String param() {
        return param;
    }

    public final int nativeInt() {
        return HASHCODE + nativeInt;
    }

    public static @Nullable
    TimeType getInstance(int nativeInt) {
        int value = nativeInt - HASHCODE;
        if (value == ONE_MINUTE.nativeInt) {
            return TimeType.ONE_MINUTE;
        } else if (value == FIVE_MINUTE.nativeInt) {
            return TimeType.FIVE_MINUTE;
        } else if (value == FIFTEEN_MINUTE.nativeInt) {
            return TimeType.FIFTEEN_MINUTE;
        } else if (value == THIRTY_MINUTE.nativeInt) {
            return TimeType.THIRTY_MINUTE;
        } else if (value == ONE_HOUR.nativeInt) {
            return TimeType.ONE_HOUR;
        } else if (value == TWO_HOUR.nativeInt) {
            return TimeType.TWO_HOUR;
        } else if (value == THREE_HOUR.nativeInt) {
            return TimeType.THREE_HOUR;
        } else if (value == FOUR_HOUR.nativeInt) {
            return TimeType.FOUR_HOUR;
        } else if (value == SIX_HOUR.nativeInt) {
            return TimeType.SIX_HOUR;
        } else if (value == EIGHT_HOUR.nativeInt) {
            return TimeType.EIGHT_HOUR;
        } else if (value == TWELVE_HOUR.nativeInt) {
            return TimeType.TWELVE_HOUR;
        } else if (value == DAY.nativeInt) {
            return TimeType.DAY;
        } else if (value == WEEK.nativeInt) {
            return TimeType.WEEK;
        } else if (value == MONTH.nativeInt) {
            return TimeType.MONTH;
        }
        return null;
    }

    public static @Nullable
    TimeType getInstance(@Nullable String param) {
        if (ONE_MINUTE.param.equals(param)) {
            return TimeType.ONE_MINUTE;
        } else if (FIVE_MINUTE.param.equals(param)) {
            return TimeType.FIVE_MINUTE;
        } else if (FIFTEEN_MINUTE.param.equals(param)) {
            return TimeType.FIFTEEN_MINUTE;
        } else if (THIRTY_MINUTE.param.equals(param)) {
            return TimeType.THIRTY_MINUTE;
        } else if (ONE_HOUR.param.equals(param)) {
            return TimeType.ONE_HOUR;
        } else if (TWO_HOUR.param.equals(param)) {
            return TimeType.TWO_HOUR;
        } else if (THREE_HOUR.param.equals(param)) {
            return TimeType.THREE_HOUR;
        } else if (FOUR_HOUR.param.equals(param)) {
            return TimeType.FOUR_HOUR;
        } else if (SIX_HOUR.param.equals(param)) {
            return TimeType.SIX_HOUR;
        } else if (EIGHT_HOUR.param.equals(param)) {
            return TimeType.EIGHT_HOUR;
        } else if (TWELVE_HOUR.param.equals(param)) {
            return TimeType.TWELVE_HOUR;
        } else if (DAY.param.equals(param)) {
            return TimeType.DAY;
        } else if (WEEK.param.equals(param)) {
            return TimeType.WEEK;
        } else if (MONTH.param.equals(param)) {
            return TimeType.MONTH;
        }
        return null;
    }
}
