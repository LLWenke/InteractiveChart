package com.wk.chart.entry;

/**
 * 比率实例
 */
public class RateEntry {
    private double rate;  //比率
    private String sign; //标识
    private int scale;//精度

    public RateEntry(Double rate, String unit, int scale) {
        setRate(rate);
        setSign(unit);
        setScale(scale);
    }

    public double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = null == rate ? 1.0 : rate;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = null == sign ? "" : sign;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * 是否设置(true:已设置 false:未设置)
     */
    public boolean isSet() {
        return getRate() != 1.0;
    }
}
