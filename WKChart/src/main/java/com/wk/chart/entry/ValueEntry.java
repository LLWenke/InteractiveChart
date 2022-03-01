package com.wk.chart.entry;

public class ValueEntry {
    public String source = "0";//数据源
    public String text = "0";//显示文本
    public long result = 0;//用于计算的值
    public float value = 0;//用于绘制的值
    public Integer scale = null;//精度

    public ValueEntry(String source) {
        this.source = source;
    }

    public ValueEntry() {
    }
}
