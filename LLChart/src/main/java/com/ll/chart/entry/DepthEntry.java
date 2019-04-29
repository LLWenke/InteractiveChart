
package com.ll.chart.entry;

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>DepthEntry</p>
 */

public class DepthEntry extends AbsEntry {
  // 初始需全部赋值的属性
  private final ValueEntry price;
  private final ValueEntry amount;
  private final ValueEntry totalAmount;
  private ValueEntry totalPrice;
  private int type;

  /**
   * @param time 时间
   * @param scale 精度
   * @param quoteScale quote精度
   * @param price 价格
   * @param amount 交易量
   * @param totalAmount 总交易量
   * @param type 类型
   */
  public DepthEntry(Date time, int scale, int quoteScale, double price, double amount,
      double totalAmount, int type) {
    super(time, scale);
    this.price = buildValue(price, quoteScale);
    this.amount = buildValue(amount, quoteScale);
    this.totalAmount = buildValue(totalAmount);
    this.type = type;
    resetTotalPrice(quoteScale);
    addAnimatorEntry(this.price, this.totalAmount);
  }

  /**
   * @param time 时间
   * @param scale 精度
   * @param quoteScale quote精度
   * @param price 价格
   * @param amount 交易量
   * @param totalAmount 总交易量
   * @param type 类型
   */
  public DepthEntry(Date time, int scale, int quoteScale, BigDecimal price, BigDecimal amount,
      BigDecimal totalAmount, int type) {
    super(time, scale);
    this.price = buildValue(price, quoteScale);
    this.amount = buildValue(amount, quoteScale);
    this.totalAmount = buildValue(totalAmount);
    this.type = type;
    resetTotalPrice(quoteScale);
    addAnimatorEntry(this.price, this.totalAmount);
  }

  /**
   * @param time 时间
   * @param scale 精度
   * @param quoteScale quote精度
   * @param price 价格
   * @param amount 交易量
   * @param totalAmount 总交易量
   * @param type 类型
   */
  public DepthEntry(Date time, int scale, int quoteScale, long price, long amount,
      long totalAmount, int type) {
    super(time, scale);
    this.price = recoveryValue(price, quoteScale);
    this.amount = recoveryValue(amount, quoteScale);
    this.totalAmount = recoveryValue(totalAmount);
    this.type = type;
    resetTotalPrice(quoteScale);
  }

  public ValueEntry getPrice() {
    return price;
  }

  public ValueEntry getAmount() {
    return amount;
  }

  public ValueEntry getTotalAmount() {
    return totalAmount;
  }

  public ValueEntry getTotalPrice() {
    return totalPrice;
  }

  public int getType() {
    return type;
  }

  private void resetTotalPrice(int quoteScale) {
    this.totalPrice = recoveryValue(getTotalAmount().result * getPrice().result
        / (long) Math.pow(10, getScale()), quoteScale);
  }
}