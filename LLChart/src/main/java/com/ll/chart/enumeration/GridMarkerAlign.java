package com.ll.chart.enumeration;

/**
 * <p>GridMarkerAlign</p>
 */

public enum GridMarkerAlign {
  LEFT(0),//靠左

  RIGHT(1),//靠右

  AUTO(2);//自动

  GridMarkerAlign(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
