package com.ll.chart.enumeration;

/**
 * <p>ModuleType</p>
 * 图表类型
 */

public enum ModuleType {
  FLOAT(0),//浮动/跨区域 指标

  CANDLE(1),//k线图 指标

  VOLUME(2),//交易量 指标

  MACD(3),//MACD 指标

  KDJ(4),//KDJ 指标

  RSI(5),//RSI 指标

  BOLL(6),//BOLL 指标

  TIME(7),//分时图 指标

  DEPTH(8);//深度图 指标

  ModuleType(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
