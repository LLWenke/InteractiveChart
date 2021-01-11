package com.wk.chart.compat;

import com.wk.chart.enumeration.TimeType;
import java.util.Date;

/**
 * 时间格式化器
 */

public class DisplayTypeUtils {

  public static String format(Date date, TimeType displayType) {
    return DateUtil.formatDateToString(date, displayType.pattern());
  }

  public static String selectorFormat(Date date, TimeType displayType) {
    return DateUtil.formatDateToString(date, displayType == TimeType.day
        ? DateUtil.DATE_FORMAT_YMD : DateUtil.DATE_FORMAT_YMDHM);
  }
}
