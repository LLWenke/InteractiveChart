package com.wk.chart.entry;

import java.math.BigDecimal;

/**
 * 比率实例
 */
public class RateEntry {
    private BigDecimal rate;  //比率
    private String unit; //单位
    private int scale;//精度

    public RateEntry(BigDecimal rate, String unit, int scale) {
        setRate(rate);
        setUnit(unit);
        setScale(scale);
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = null == rate ? BigDecimal.ONE : rate;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = null == unit ? "" : unit;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }
}
