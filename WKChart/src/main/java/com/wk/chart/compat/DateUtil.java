package com.wk.chart.compat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 */
public class DateUtil {

    private DateUtil() {
    }

    /**
     * 常用变量
     */
    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YMD_HM = "yyyy-MM-dd HH:mm";

    /**
     * 日期转换为制定格式字符串
     */
    public static String formatDateToString(Date time, String pattern) {
        try {
            return (new SimpleDateFormat(pattern, Locale.getDefault())).format(time);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 计算两个日期相差的时间（天以内，包含天）
     */
    public static long getDateDiff(Date startDate, Date endDate, long type) {
        if (null == startDate || null == endDate) {
            return 0;
        }
        return (int) ((endDate.getTime() - startDate.getTime()) / type);
    }

    /**
     * 获取两个日期相差的月数
     *
     * @param startDate 较大的日期
     * @param endDate   较小的日期
     * @return 返回 月数差(含正负数) 否则返回0
     */
    public static int getMonthDiff(Date startDate, Date endDate) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(endDate);
        c2.setTime(startDate);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值
        int yearInterval = year1 - year2;
        // 获取月数差值
        int monthInterval = month1 + (yearInterval * 12) - month2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || (month1 == month2 && day1 < day2)) yearInterval--;
        if (day1 < day2) monthInterval--;
        monthInterval = monthInterval - (yearInterval * 12);
        return yearInterval * 12 + monthInterval;
    }
}

