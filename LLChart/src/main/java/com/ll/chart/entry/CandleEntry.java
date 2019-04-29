
package com.ll.chart.entry;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>CandleEntry</p>
 */

public class CandleEntry extends AbsEntry {

  // 初始需全部赋值的属性
  private final ValueEntry open; // 开盘价
  private final ValueEntry high; // 最高价
  private final ValueEntry low; // 最低价
  private final ValueEntry close; // 收盘价
  private final ValueEntry volume; // 量
  private final ValueEntry changeAmount; // 涨跌额
  private final ValueEntry changeProportion; // 涨跌幅

  // MA 指标的三个属性
  private ValueEntry ma5;
  private ValueEntry ma10;
  private ValueEntry ma20;

  // 量的5日平均和10日平均
  private ValueEntry volumeMa5;
  private ValueEntry volumeMa10;

  // MACD 指标的三个属性
  private ValueEntry dea;
  private ValueEntry diff;
  private ValueEntry macd;

  // KDJ 指标的三个属性
  private ValueEntry k;
  private ValueEntry d;
  private ValueEntry j;

  // RSI 指标的三个属性
  private ValueEntry rsi1;
  private ValueEntry rsi2;
  private ValueEntry rsi3;

  // BOLL 指标的三个属性
  private ValueEntry up; // 上轨线
  private ValueEntry mb; // 中轨线
  private ValueEntry dn; // 下轨线

  /**
   * 自定义 K 线图用的数据
   *
   * @param scale 精度
   * @param open 开盘价
   * @param high 最高价
   * @param low 最低价
   * @param close 收盘价
   * @param volume 量
   * @param time 时间
   */
  public CandleEntry(int scale, double open, double high, double low, double close,
      double volume, Date time) {
    super(time, scale);
    this.open = buildValue(open);
    this.high = buildValue(high);
    this.low = buildValue(low);
    this.close = buildValue(close);
    this.volume = buildValue(volume);
    this.changeAmount = recoveryValue(getClose().result - getOpen().result);
    this.changeProportion = recoveryValue(getChangeAmount().result * 10000 / getOpen().result, 2);
    addAnimatorEntry(this.close, this.high, this.low, this.volume);
  }

  /**
   * 自定义 K 线图用的数据
   *
   * @param scale 精度
   * @param open 开盘价
   * @param high 最高价
   * @param low 最低价
   * @param close 收盘价
   * @param volume 量
   * @param time 时间
   */
  public CandleEntry(int scale, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close,
      BigDecimal volume, Date time) {
    super(time, scale);
    this.open = buildValue(open);
    this.high = buildValue(high);
    this.low = buildValue(low);
    this.close = buildValue(close);
    this.volume = buildValue(volume);
    this.changeAmount = recoveryValue(getClose().result - getOpen().result);
    this.changeProportion = recoveryValue(getChangeAmount().result * 10000 / getOpen().result, 2);
    addAnimatorEntry(this.close, this.high, this.low, this.volume);
  }

  //set 方法
  public void setMa5(long ma5) {
    this.ma5 = recoveryValue(ma5);
  }

  public void setMa10(long ma10) {
    this.ma10 = recoveryValue(ma10);
  }

  public void setMa20(long ma20) {
    this.ma20 = recoveryValue(ma20);
  }

  public void setVolumeMa5(long volumeMa5) {
    this.volumeMa5 = recoveryValue(volumeMa5);
  }

  public void setVolumeMa10(long volumeMa10) {
    this.volumeMa10 = recoveryValue(volumeMa10);
  }

  public void setDea(long dea) {
    this.dea = recoveryValue(dea);
  }

  public void setDiff(long diff) {
    this.diff = recoveryValue(diff);
  }

  public void setMacd(long macd) {
    this.macd = recoveryValue(macd);
  }

  public void setK(long k) {
    this.k = recoveryValue(k);
  }

  public void setD(long d) {
    this.d = recoveryValue(d);
  }

  public void setJ(long j) {
    this.j = recoveryValue(j);
  }

  public void setRsi1(long rsi1) {
    this.rsi1 = recoveryValue(rsi1);
  }

  public void setRsi2(long rsi2) {
    this.rsi2 = recoveryValue(rsi2);
  }

  public void setRsi3(long rsi3) {
    this.rsi3 = recoveryValue(rsi3);
  }

  public void setUp(long up) {
    this.up = recoveryValue(up);
  }

  public void setMb(long mb) {
    this.mb = recoveryValue(mb);
  }

  public void setDn(long dn) {
    this.dn = recoveryValue(dn);
  }

  public ValueEntry getOpen() {
    return open;
  }

  public ValueEntry getHigh() {
    return high;
  }

  public ValueEntry getLow() {
    return low;
  }

  public ValueEntry getClose() {
    return close;
  }

  public ValueEntry getVolume() {
    return volume;
  }

  public ValueEntry getChangeAmount() {
    return changeAmount;
  }

  public ValueEntry getChangeProportion() {
    return changeProportion;
  }

  public ValueEntry getMa5() {
    return ma5;
  }

  public ValueEntry getMa10() {
    return ma10;
  }

  public ValueEntry getMa20() {
    return ma20;
  }

  public ValueEntry getVolumeMa5() {
    return volumeMa5;
  }

  public ValueEntry getVolumeMa10() {
    return volumeMa10;
  }

  public ValueEntry getDea() {
    return dea;
  }

  public ValueEntry getDiff() {
    return diff;
  }

  public ValueEntry getMacd() {
    return macd;
  }

  public ValueEntry getK() {
    return k;
  }

  public ValueEntry getD() {
    return d;
  }

  public ValueEntry getJ() {
    return j;
  }

  public ValueEntry getRsi1() {
    return rsi1;
  }

  public ValueEntry getRsi2() {
    return rsi2;
  }

  public ValueEntry getRsi3() {
    return rsi3;
  }

  public ValueEntry getUp() {
    return up;
  }

  public ValueEntry getMb() {
    return mb;
  }

  public ValueEntry getDn() {
    return dn;
  }

  @Override
  public String toString() {
    return "\nCandleEntry{" +
        "open=" + getOpen().text +
        ", high=" + getHigh().text +
        ", low=" + getLow().text +
        ", close=" + getClose().text +
        ", volume=" + getVolume().text +
        ", time=" + getTime() +
        '}';
  }
}
