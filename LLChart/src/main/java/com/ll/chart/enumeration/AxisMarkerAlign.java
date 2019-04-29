package com.ll.chart.enumeration;

/**
 * <p>AxisMarkerAlign</p>
 */

public enum AxisMarkerAlign {
  TOP(0),

  BOTTOM(1),

  AUTO(2);

  AxisMarkerAlign(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
