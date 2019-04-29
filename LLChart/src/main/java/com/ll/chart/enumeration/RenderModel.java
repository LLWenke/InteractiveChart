package com.ll.chart.enumeration;

/**
 * <p>RenderModel</p>
 * 图表渲染模式
 */

public enum RenderModel {
  CANDLE(0),//蜡烛图

  DEPTH(1);//深度图

  RenderModel(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
