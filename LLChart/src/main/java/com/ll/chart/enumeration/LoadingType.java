package com.ll.chart.enumeration;

/**
 * <p>LoadingType</p>
 * 图表加载模式
 */

public enum LoadingType {
  LEFT_LOADING(0),//左滑动加载

  RIGHT_LOADING(1),//右滑动加载

  REFRESH_LOADING(2);//刷新

  LoadingType(int nativeInt) {
    this.nativeInt = nativeInt;
  }

  final int nativeInt;
}
