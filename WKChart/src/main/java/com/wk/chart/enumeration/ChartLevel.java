package com.wk.chart.enumeration;

/**
 * <p>ChartLevel</p>
 * 图表级别
 */

public enum ChartLevel {
  MAIN(0),//主图

  AUXILIARY(1);//副图

  ChartLevel(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
