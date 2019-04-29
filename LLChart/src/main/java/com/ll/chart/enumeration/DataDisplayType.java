package com.ll.chart.enumeration;

/**
 * <p>DataDisplayType</p>
 * 数据展示类型
 */

public enum DataDisplayType {
  PAGING(0),//分页

  REAL_TIME(1);//实时

  DataDisplayType(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
