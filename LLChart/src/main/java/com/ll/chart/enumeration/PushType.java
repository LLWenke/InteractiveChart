package com.ll.chart.enumeration;

/**
 * <p>PushType</p>
 * 推送类型
 */

public enum PushType {
  UPDATE(0),//更改

  ADD(1),//添加

  INTERMITTENT(2),//间断

  INVALID(3);//无效

  PushType(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
