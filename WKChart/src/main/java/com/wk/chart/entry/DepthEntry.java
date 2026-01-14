package com.wk.chart.entry;

import androidx.annotation.NonNull;

import com.wk.chart.formatter.ValueFormatter;
import com.wk.utils.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>DepthEntry</p>
 */

public class DepthEntry extends AbsEntry {
    private final ValueEntry price;// 价格
    private final ValueEntry amount;// 交易量
    private final ValueEntry totalAmount;// 总交易量
    private final ValueEntry totalPrice;// 总交易额
    private final int type;

    /**
     * @param price       价格
     * @param amount      交易量
     * @param totalAmount 总交易量
     * @param type        类型
     * @param time        时间
     */
    public DepthEntry(String price, String amount, String totalAmount, int type, @NonNull Date time) {
        super(time);
        this.price = new ValueEntry(NumberUtils.parseDouble(price));
        this.amount = new ValueEntry(NumberUtils.parseDouble(amount));
        this.totalAmount = new ValueEntry(NumberUtils.parseDouble(totalAmount));
        this.totalPrice = new ValueEntry(0d);
        this.type = type;
    }

    /**
     * @param price       价格
     * @param amount      交易量
     * @param totalAmount 总交易量
     * @param type        类型
     * @param time        时间
     */
    public DepthEntry(double price, double amount, double totalAmount, int type, @NonNull Date time) {
        super(time);
        this.price = new ValueEntry(price);
        this.amount = new ValueEntry(amount);
        this.totalAmount = new ValueEntry(totalAmount);
        this.totalPrice = new ValueEntry(0d);
        this.type = type;
    }

    /**
     * 构建值精度(耗时操作，建议放在子线程)
     *
     * @param scale     精度
     * @param formatter 数值格式化工具
     */
    public void buildValueScale(@NonNull ScaleEntry scale, @NonNull ValueFormatter formatter) {
        int baseScale = scale.getBaseScale();
        int quoteScale = scale.getQuoteScale();
        BigDecimal priceValue = NumberUtils.parseBigDecimal(price.value, quoteScale);
        BigDecimal amountValue = NumberUtils.parseBigDecimal(amount.value, quoteScale);
        BigDecimal totalAmountValue = NumberUtils.parseBigDecimal(totalAmount.value, baseScale);
        BigDecimal totalPriceValue = priceValue.multiply(amountValue).setScale(quoteScale, RoundingMode.DOWN);
        this.price.value = priceValue.doubleValue();
        this.price.valueFormat = formatter.formatFixed(priceValue, quoteScale,false);
        this.amount.value = amountValue.doubleValue();
        this.amount.valueFormat = formatter.formatFixed(amountValue, baseScale,false);
        this.totalAmount.value = totalAmountValue.doubleValue();
        this.totalAmount.valueFormat = formatter.formatFixed(totalAmountValue, baseScale,false);
        this.totalPrice.value = totalPriceValue.doubleValue();
        this.totalPrice.valueFormat = formatter.formatFixed(totalPriceValue, quoteScale,false);
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

    @Override
    public List<ValueEntry> getAnimatorEntry() {
        return Collections.emptyList();
    }
}