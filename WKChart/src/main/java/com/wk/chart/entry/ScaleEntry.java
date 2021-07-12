package com.wk.chart.entry;

import java.io.Serializable;

public class ScaleEntry implements Serializable {
    private int baseScale;// base 精度
    private int quoteScale;// quote 精度
    private String baseUnit;// base 单位
    private String quoteUnit;// quote 单位

    public ScaleEntry(int baseScale, int quoteScale, String baseUnit, String quoteUnit) {
        this.baseScale = baseScale;
        this.quoteScale = quoteScale;
        this.baseUnit = baseUnit;
        this.quoteUnit = quoteUnit;
    }

    public ScaleEntry(int baseScale, int quoteScale) {
        this.baseScale = baseScale;
        this.quoteScale = quoteScale;
        this.baseUnit = "";
        this.quoteUnit = "";
    }

    public void reset(int baseScale, int quoteScale) {
        this.baseScale = baseScale;
        this.quoteScale = quoteScale;
    }

    public void reset(int baseScale, int quoteScale, String baseUnit, String quoteUnit) {
        this.baseScale = baseScale;
        this.quoteScale = quoteScale;
        this.baseUnit = baseUnit;
        this.quoteUnit = quoteUnit;
    }

    /**
     * 获取Base精度
     */
    public int getBaseScale() {
        return baseScale;
    }

    /**
     * 获取Quote精度
     */
    public int getQuoteScale() {
        return quoteScale;
    }

    /**
     * 设置Base单位
     */
    public String getBaseUnit() {
        return baseUnit;
    }

    /**
     * 设置Quote单位
     */
    public String getQuoteUnit() {
        return quoteUnit;
    }

}