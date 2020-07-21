package com.wk.chart.entry;

public class ValueEntry {
    public ValueEntry(int scale) {
        this.scale = scale;
    }

    public String text = "0";//显示文本
    public long result = 0;//用于计算的值
    public float value = 0;//用于绘制的值
    private int scale;//精度

    public int getScale() {
        return scale;
    }
}
