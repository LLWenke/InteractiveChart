package com.wk.chart.enumeration;

import androidx.annotation.Nullable;

import java.io.Serializable;

/**
 * <p>DisplayType</p>
 * 图表时间类型
 */

public enum DisplayType implements Serializable {
    oneMinute(0, "MM-dd HH:mm", 1, 60000, "1min"),//一分钟

    fiveMinute(1, oneMinute.pattern, 5, oneMinute.msec, "5min"),//五分钟

    fifteenMinute(2, oneMinute.pattern, 15, oneMinute.msec, "15min"),//十五分钟

    thirtyMinute(3, oneMinute.pattern, 30, oneMinute.msec, "30min"),//三十分钟

    oneHour(4, oneMinute.pattern, 1, oneMinute.msec * 60, "60min"),//一小时

    twoHour(5, oneMinute.pattern, 2, oneHour.msec, "2hour"),//二小时

    threeHour(6, oneMinute.pattern, 3, oneHour.msec, "3hour"),//三小时

    fourHour(7, oneMinute.pattern, 4, oneHour.msec, "4hour"),//四小时

    sixHour(8, oneMinute.pattern, 6, oneHour.msec, "6hour"),//六小时

    eightHour(9, oneMinute.pattern, 8, oneHour.msec, "8hour"),//八小时

    twelveHour(10, oneMinute.pattern, 12, oneHour.msec, "12hour"),//十二小时

    day(11, "yy-MM-dd", 1, oneHour.msec * 24, "1day"),//天

    week(12, "yy-MM-dd", 7, day.msec, "1week"),//周

    month(13, "yy-MM-dd", 1, 0, "1month");//月

    DisplayType(int nativeInt, String pattern, int value, long msec, String param) {
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
    private static final int hashCode = DisplayType.class.hashCode();

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
    DisplayType getInstance(int nativeInt) {
        int value = nativeInt - hashCode;
        if (value == oneMinute.nativeInt) {
            return DisplayType.oneMinute;
        } else if (value == fiveMinute.nativeInt) {
            return DisplayType.fiveMinute;
        } else if (value == fifteenMinute.nativeInt) {
            return DisplayType.fifteenMinute;
        } else if (value == thirtyMinute.nativeInt) {
            return DisplayType.thirtyMinute;
        } else if (value == oneHour.nativeInt) {
            return DisplayType.oneHour;
        } else if (value == twoHour.nativeInt) {
            return DisplayType.twoHour;
        } else if (value == threeHour.nativeInt) {
            return DisplayType.threeHour;
        } else if (value == fourHour.nativeInt) {
            return DisplayType.fourHour;
        } else if (value == sixHour.nativeInt) {
            return DisplayType.sixHour;
        } else if (value == eightHour.nativeInt) {
            return DisplayType.eightHour;
        } else if (value == twelveHour.nativeInt) {
            return DisplayType.twelveHour;
        } else if (value == day.nativeInt) {
            return DisplayType.day;
        } else if (value == week.nativeInt) {
            return DisplayType.week;
        } else if (value == month.nativeInt) {
            return DisplayType.month;
        }
        return null;
    }

    public static @Nullable
    DisplayType getInstance(String param) {
        if (param.equals(oneMinute.param)) {
            return DisplayType.oneMinute;
        } else if (param.equals(fiveMinute.param)) {
            return DisplayType.fiveMinute;
        } else if (param.equals(fifteenMinute.param)) {
            return DisplayType.fifteenMinute;
        } else if (param.equals(thirtyMinute.param)) {
            return DisplayType.thirtyMinute;
        } else if (param.equals(oneHour.param)) {
            return DisplayType.oneHour;
        } else if (param.equals(twoHour.param)) {
            return DisplayType.twoHour;
        } else if (param.equals(threeHour.param)) {
            return DisplayType.threeHour;
        } else if (param.equals(fourHour.param)) {
            return DisplayType.fourHour;
        } else if (param.equals(sixHour.param)) {
            return DisplayType.sixHour;
        } else if (param.equals(eightHour.param)) {
            return DisplayType.eightHour;
        } else if (param.equals(twelveHour.param)) {
            return DisplayType.twelveHour;
        } else if (param.equals(day.param)) {
            return DisplayType.day;
        } else if (param.equals(week.param)) {
            return DisplayType.week;
        } else if (param.equals(month.param)) {
            return DisplayType.month;
        }
        return null;
    }
}
