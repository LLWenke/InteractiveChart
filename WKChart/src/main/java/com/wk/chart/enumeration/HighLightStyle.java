package com.wk.chart.enumeration;

/**
 * <p>DataType</p>
 * 数据展示类型
 */

public enum HighLightStyle {
  SOLID(0),//实线

  DOTTED(1);//虚线

  HighLightStyle(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
