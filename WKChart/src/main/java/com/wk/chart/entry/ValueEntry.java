package com.wk.chart.entry;

import com.wk.chart.formatter.ValueFormatter;

public class ValueEntry {
    public double value;//数字值
    public String valueFormat = "0";//格式化值

    /**
     * 构造方法
     *
     * @param value 数字值
     */
    public ValueEntry(double value) {
        this.value = value;
    }

    /**
     * 格式化(精度)
     *
     * @param formatter 格式化器
     * @param scale     精度
     * @return ValueEntry
     */
    public ValueEntry formatFixed(ValueFormatter formatter, int scale) {
        this.valueFormat = formatter.formatFixed(value, scale);
        return this;
    }

    /**
     * 带单位格式化(精度)
     *
     * @param formatter 格式化器
     * @param scale     精度
     * @return ValueEntry
     */
    public ValueEntry formatUnit(ValueFormatter formatter, int scale) {
        this.valueFormat = formatter.formatUnit(value, scale);
        return this;
    }
}
