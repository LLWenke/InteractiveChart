package com.wk.chart.enumeration;

/**
 * 通知类型
 */
public enum ObserverArg {
  update(0),//更新

  normal(1),//无改变

  init(2);//初始化

  ObserverArg(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
