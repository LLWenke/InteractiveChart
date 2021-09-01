package com.wk.chart.entry;

/**
 * 量化实例
 */
public class QuantizationEntry {
    private long minFormatNum = 9999;//最小量化数（即小于此数，不量化）
    private int scale = 2;//量化后小数的精度

    public long getMinFormatNum() {
        return minFormatNum;
    }

    public int getScale() {
        return scale;
    }

    public void reset(long minFormatNum, int scale) {
        this.minFormatNum = minFormatNum;
        this.scale = scale;
    }
}
