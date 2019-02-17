package com.wk.chart.enumeration;

/**
 * <p>DisplayType</p>
 * 图表时间类型
 */

public enum DisplayType {
  oneMinute("MM-dd HH:mm", 1, 60000),//一分钟

  fiveMinute(oneMinute.pattern, 5, oneMinute.value),//五分钟

  fifteenMinute(oneMinute.pattern, 15, oneMinute.value),//十五分钟

  thirtyMinute(oneMinute.pattern, 30, oneMinute.value),//三十分钟

  oneHour(oneMinute.pattern, 1, oneMinute.value * 60),//一小时

  threeHour(oneMinute.pattern, 3, oneHour.value),//三小时

  sixHour(oneMinute.pattern, 6, oneHour.value),//六小时

  oneDay("MM-dd", 1, oneHour.value * 24);//一天

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
