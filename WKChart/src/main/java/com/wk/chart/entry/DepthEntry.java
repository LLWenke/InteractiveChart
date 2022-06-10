package com.wk.chart.entry;

import androidx.annotation.NonNull;

import com.wk.chart.compat.ValueUtils;

import java.util.Date;

/**
 * <p>DepthEntry</p>
 */

public class DepthEntry extends AbsEntry {
    // 初始需全部赋值的属性
    private final ValueEntry price;
    private final ValueEntry amount;
    private final ValueEntry totalAmount;
    private final ValueEntry totalPrice;
    private final int type;

    /**
     * @param price       价格
     * @param amount      交易量
     * @param totalAmount 总交易量
     * @param type        类型
     * @param time        时间
     */
    public DepthEntry(String price, String amount, String totalAmount, int type,@NonNull Date time) {
        super(time);
        this.price = new ValueEntry(price);
        this.amount = new ValueEntry(amount);
        this.totalAmount = new ValueEntry(totalAmount);
        this.totalPrice = new ValueEntry();
        this.type = type;
    }

    /**
     * @param priceResult       价格
     * @param amountResult      交易量
     * @param totalAmountResult 总交易量
     * @param type              类型
     * @param time              时间
     */
    public DepthEntry(@NonNull ScaleEntry scale, Long priceResult, Long amountResult,
                      Long totalAmountResult, int type,@NonNull Date time) {
        super(time);
        this.price = new ValueEntry();
        this.amount = new ValueEntry();
        this.totalAmount = new ValueEntry();
        this.totalPrice = new ValueEntry();
        this.type = type;
        ValueUtils.buildScaleValue(price, priceResult, scale.getQuoteScale());
        ValueUtils.buildScaleValue(amount, amountResult, scale.getBaseScale());
        ValueUtils.buildScaleValue(totalAmount, totalAmountResult, scale.getBaseScale());
        buildTotalPriceScaleValue(scale);
    }

    /**
     * 构建精度值
     *
     * @param scale 精度
     */
    public void buildScaleValue(@NonNull ScaleEntry scale) {
        Integer priceScale = scale.getQuoteScale();
        if (!priceScale.equals(price.scale)) {
            ValueUtils.buildScaleValue(price, priceScale);
        }
        Integer amountScale = scale.getBaseScale();
        if (!amountScale.equals(amount.scale)) {
            ValueUtils.buildScaleValue(amount, amountScale);
        }
        Integer totalAmountScale = scale.getBaseScale();
        if (!totalAmountScale.equals(totalAmount.scale)) {
            ValueUtils.buildScaleValue(totalAmount, totalAmountScale);
        }
        buildTotalPriceScaleValue(scale);
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

    private void buildTotalPriceScaleValue(@NonNull ScaleEntry scale) {
        Integer totalPriceScale = scale.getQuoteScale();
        long totalPriceValue = ValueUtils.scaleMultiply(price.result, totalAmount.result, totalAmount.scale);
        if (!totalPriceScale.equals(totalPrice.scale) || totalPriceValue != totalPrice.result) {
            ValueUtils.buildScaleValue(totalPrice, totalPriceValue, totalPriceScale);
        }
    }
}