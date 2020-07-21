package com.wk.chart.enumeration;

/**
 * <p>GridMarkerAlign</p>
 */

public enum GridMarkerAlign {
  TOP(0),

  BOTTOM(1),

  TOP_INSIDE(2),//上（内部）

  BOTTOM_INSIDE(3),//下（内部）

  AUTO(4);

  GridMarkerAlign(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
