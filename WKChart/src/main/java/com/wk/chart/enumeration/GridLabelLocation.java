package com.wk.chart.enumeration;

/**
 * <p>GridLabelLocation</p>
 */

public enum GridLabelLocation {
  LEFT(0),//左边

  RIGHT(1),//右边

  ALL(2);//左右都有

  GridLabelLocation(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
