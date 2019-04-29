package com.ll.chart.enumeration;

/**
 * <p>AxisLabelLocation</p>
 */

public enum AxisLabelLocation {
  LEFT(0),//左边

  RIGHT(1),//右边

  ALL(2);//左右都有

  AxisLabelLocation(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
