package com.ll.chart.enumeration;

/**
 * <p>DisplayType</p>
 * 图表时间类型
 */

public enum DisplayType {
  oneMinute("MM-dd HH:mm", 1, 60000),//一分钟

  fiveMinute(oneMinute.pattern, 5, oneMinute.msec),//五分钟

  fifteenMinute(oneMinute.pattern, 15, oneMinute.msec),//十五分钟

  thirtyMinute(oneMinute.pattern, 30, oneMinute.msec),//三十分钟

  oneHour(oneMinute.pattern, 1, oneMinute.msec * 60),//一小时

  threeHour(oneMinute.pattern, 3, oneHour.msec),//三小时

  sixHour(oneMinute.pattern, 6, oneHour.msec),//六小时

  oneDay("MM-dd", 1, oneHour.msec * 24);//一天

  DisplayType(String pattern, int value, long msec) {
    this.pattern = pattern;
    this.value = value;
    this.msec = msec;
  }

  private final String pattern;
  private final int value;
  private final long msec;

  public String pattern() {
    return pattern;
  }

  public int value() {
    return value;
  }

  public long msec() {
    return msec;
  }
}
