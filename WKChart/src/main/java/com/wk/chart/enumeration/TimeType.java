package com.wk.chart.enumeration;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * <p>TimeType</p>
 * 图表时间类型
 */

public enum TimeType implements Serializable {
    oneMinute(0, "HH:mm", 1, 60000, "1min"),//一分钟

    fiveMinute(1, oneMinute.pattern, 5, oneMinute.msec, "5min"),//五分钟

    fifteenMinute(2, oneMinute.pattern, 15, oneMinute.msec, "15min"),//十五分钟

    thirtyMinute(3, oneMinute.pattern, 30, oneMinute.msec, "30min"),//三十分钟

    oneHour(4, oneMinute.pattern, 1, oneMinute.msec * 60, "60min"),//一小时

    twoHour(5, "MM-dd HH:mm", 2, oneHour.msec, "2hour"),//二小时

    threeHour(6, twoHour.pattern, 3, oneHour.msec, "3hour"),//三小时

    fourHour(7, twoHour.pattern, 4, oneHour.msec, "4hour"),//四小时

    sixHour(8, twoHour.pattern, 6, oneHour.msec, "6hour"),//六小时

    eightHour(9, twoHour.pattern, 8, oneHour.msec, "8hour"),//八小时

    twelveHour(10, twoHour.pattern, 12, oneHour.msec, "12hour"),//十二小时

    day(11, "MM-dd", 1, oneHour.msec * 24, "1day"),//天

    week(12, "yy-MM-dd", 7, day.msec, "1week"),//周

    month(13, "yy-MM-dd", 1, 0, "1month");//月

    TimeType(int nativeInt, String pattern, int value, long msec, String param) {
        this.pattern = pattern;
        this.value = value;
        this.msec = msec;
        this.nativeInt = nativeInt;
        this.param = param;
    }

    private final String pattern;
    private final int value;
    private final long msec;
    private final int nativeInt;
    private final String param;
    private static final int hashCode = TimeType.class.hashCode();

    public final String pattern() {
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
        return hashCode + nativeInt;
    }

    public static @Nullable
    TimeType getInstance(int nativeInt) {
        int value = nativeInt - hashCode;
        if (value == oneMinute.nativeInt) {
            return TimeType.oneMinute;
        } else if (value == fiveMinute.nativeInt) {
            return TimeType.fiveMinute;
        } else if (value == fifteenMinute.nativeInt) {
            return TimeType.fifteenMinute;
        } else if (value == thirtyMinute.nativeInt) {
            return TimeType.thirtyMinute;
        } else if (value == oneHour.nativeInt) {
            return TimeType.oneHour;
        } else if (value == twoHour.nativeInt) {
            return TimeType.twoHour;
        } else if (value == threeHour.nativeInt) {
            return TimeType.threeHour;
        } else if (value == fourHour.nativeInt) {
            return TimeType.fourHour;
        } else if (value == sixHour.nativeInt) {
            return TimeType.sixHour;
        } else if (value == eightHour.nativeInt) {
            return TimeType.eightHour;
        } else if (value == twelveHour.nativeInt) {
            return TimeType.twelveHour;
        } else if (value == day.nativeInt) {
            return TimeType.day;
        } else if (value == week.nativeInt) {
            return TimeType.week;
        } else if (value == month.nativeInt) {
            return TimeType.month;
        }
        return null;
    }

    public static @Nullable
    TimeType getInstance(@Nullable String param) {
        if (oneMinute.param.equals(param)) {
            return TimeType.oneMinute;
        } else if (fiveMinute.param.equals(param)) {
            return TimeType.fiveMinute;
        } else if (fifteenMinute.param.equals(param)) {
            return TimeType.fifteenMinute;
        } else if (thirtyMinute.param.equals(param)) {
            return TimeType.thirtyMinute;
        } else if (oneHour.param.equals(param)) {
            return TimeType.oneHour;
        } else if (twoHour.param.equals(param)) {
            return TimeType.twoHour;
        } else if (threeHour.param.equals(param)) {
            return TimeType.threeHour;
        } else if (fourHour.param.equals(param)) {
            return TimeType.fourHour;
        } else if (sixHour.param.equals(param)) {
            return TimeType.sixHour;
        } else if (eightHour.param.equals(param)) {
            return TimeType.eightHour;
        } else if (twelveHour.param.equals(param)) {
            return TimeType.twelveHour;
        } else if (day.param.equals(param)) {
            return TimeType.day;
        } else if (week.param.equals(param)) {
            return TimeType.week;
        } else if (month.param.equals(param)) {
            return TimeType.month;
        }
        return null;
    }
}
