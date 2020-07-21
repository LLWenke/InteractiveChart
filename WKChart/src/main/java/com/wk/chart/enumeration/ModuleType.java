package com.wk.chart.enumeration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <p>ModuleType</p>
 * 图表类型
 */

public enum ModuleType {
  FLOAT(0, "FLOAT"),//浮动/跨区域 指标

  CANDLE(1, "CANDLE"),//k线图 指标

  VOLUME(2, "VOLUME"),//交易量 指标

  MACD(3, "MACD"),//MACD 指标

  KDJ(4, "KDJ"),//KDJ 指标

  RSI(5, "RSI"),//RSI 指标

  BOLL(6, "BOLL"),//BOLL 指标

  TIME(7, "TIME"),//分时图 指标

  DEPTH(8, "DEPTH"),//深度图 指标

  MA(9, "MA"),//平均线 指标

  EMA(10, "EMA"),//EMA 指标

  DMI(11, "DMI"),//DMI 指标

  WR(12, "WR");//WR 指标

  ModuleType(int nativeInt, String param) {
    this.nativeInt = nativeInt;
    this.param = param;
  }

  final int nativeInt;
  final String param;

  final static int hashCode = ModuleType.class.hashCode();

  public final String param() {
    return param;
  }

  public final int nativeInt() {
    return hashCode + nativeInt;
  }

  public static @Nullable ModuleType getInstance(int nativeInt) {
    int value = nativeInt - hashCode;
    if (value == FLOAT.nativeInt) {
      return ModuleType.FLOAT;
    } else if (value == CANDLE.nativeInt) {
      return ModuleType.CANDLE;
    } else if (value == VOLUME.nativeInt) {
      return ModuleType.VOLUME;
    } else if (value == MACD.nativeInt) {
      return ModuleType.MACD;
    } else if (value == KDJ.nativeInt) {
      return ModuleType.KDJ;
    } else if (value == RSI.nativeInt) {
      return ModuleType.RSI;
    } else if (value == BOLL.nativeInt) {
      return ModuleType.BOLL;
    } else if (value == TIME.nativeInt) {
      return ModuleType.TIME;
    } else if (value == DEPTH.nativeInt) {
      return ModuleType.DEPTH;
    } else if (value == MA.nativeInt) {
      return ModuleType.MA;
    } else if (value == EMA.nativeInt) {
      return ModuleType.EMA;
    } else if (value == DMI.nativeInt) {
      return ModuleType.DMI;
    } else if (value == WR.nativeInt) {
      return ModuleType.WR;
    }

    return null;
  }

  public static @Nullable ModuleType getInstance(String param) {
    if (param.equals(FLOAT.param)) {
      return ModuleType.FLOAT;
    } else if (param.equals(CANDLE.param)) {
      return ModuleType.CANDLE;
    } else if (param.equals(VOLUME.param)) {
      return ModuleType.VOLUME;
    } else if (param.equals(MACD.param)) {
      return ModuleType.MACD;
    } else if (param.equals(KDJ.param)) {
      return ModuleType.KDJ;
    } else if (param.equals(RSI.param)) {
      return ModuleType.RSI;
    } else if (param.equals(BOLL.param)) {
      return ModuleType.BOLL;
    } else if (param.equals(TIME.param)) {
      return ModuleType.TIME;
    } else if (param.equals(DEPTH.param)) {
      return ModuleType.DEPTH;
    } else if (param.equals(MA.param)) {
      return ModuleType.MA;
    } else if (param.equals(EMA.param)) {
      return ModuleType.EMA;
    } else if (param.equals(DMI.param)) {
      return ModuleType.DMI;
    } else if (param.equals(WR.param)) {
      return ModuleType.WR;
    }

    return null;
  }
}
