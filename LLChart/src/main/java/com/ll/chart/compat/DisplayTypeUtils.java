package com.ll.chart.compat;

import com.ll.chart.enumeration.DisplayType;
import java.util.Date;

/**
 * 时间格式化器
 */

public class DisplayTypeUtils {

  public static String format(Date date, DisplayType displayType) {
    return DateUtil.formatDateToString(date, displayType.pattern());
  }

  public static String selectorFormat(Date date, DisplayType displayType) {
    return DateUtil.formatDateToString(date, displayType == DisplayType.oneDay
        ? DateUtil.DATE_FORMAT_YMD : DateUtil.DATE_FORMAT_YMDHM);
  }
}
