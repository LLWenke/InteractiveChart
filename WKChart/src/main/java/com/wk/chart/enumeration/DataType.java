package com.wk.chart.enumeration;

/**
 * <p>DataType</p>
 * 数据展示类型
 */

public enum DataType {
  PAGING(0),//分页

  REAL_TIME(1);//实时

  DataType(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
