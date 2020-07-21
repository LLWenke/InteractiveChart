package com.wk.chart.entry;

import com.wk.chart.adapter.AbsAdapter;
import com.wk.chart.compat.Utils;

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
     * @param scale       精度实例
     * @param price       价格
     * @param amount      交易量
     * @param totalAmount 总交易量
     * @param type        类型
     * @param time        时间
     */
    public DepthEntry(AbsAdapter.ScaleEntry scale, double price, double amount,
                      double totalAmount, int type, Date time) {
        super(scale, time);
        this.price = buildValue(price, scale.getQuoteScale());
        this.amount = buildValue(amount, scale.getBaseScale());
        this.totalAmount = buildValue(totalAmount, scale.getBaseScale());
        this.type = type;
        resetTotalPrice();
        addAnimatorEntry(this.price, this.totalAmount);
    }

    /**
     * @param scale       精度实例
     * @param price       价格
     * @param amount      交易量
     * @param totalAmount 总交易量
     * @param type        类型
     * @param time        时间
     */
    public DepthEntry(AbsAdapter.ScaleEntry scale, BigDecimal price, BigDecimal amount,
                      BigDecimal totalAmount, int type, Date time) {
        super(scale, time);
        this.price = buildValue(price, scale.getQuoteScale());
        this.amount = buildValue(amount, scale.getBaseScale());
        this.totalAmount = buildValue(totalAmount, scale.getBaseScale());
        this.type = type;
        resetTotalPrice();
        addAnimatorEntry(this.price, this.totalAmount);
    }

    /**
     * @param scale       精度实例
     * @param price       价格
     * @param amount      交易量
     * @param totalAmount 总交易量
     * @param type        类型
     * @param time        时间
     */
    public DepthEntry(AbsAdapter.ScaleEntry scale, long price, long amount,
                      long totalAmount, int type, Date time) {
        super(scale, time);
        this.price = recoveryValue(price, scale.getQuoteScale());
        this.amount = recoveryValue(amount, scale.getBaseScale());
        this.totalAmount = recoveryValue(totalAmount, scale.getBaseScale());
        this.type = type;
        resetTotalPrice();
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

    private void resetTotalPrice() {
        this.totalPrice = recoveryValue(getTotalAmount().result
                / Utils.divisorCorrect((long) Math.pow(10, getScale().getBaseScale()))
                * getPrice().result, getScale().getQuoteScale());
    }
}