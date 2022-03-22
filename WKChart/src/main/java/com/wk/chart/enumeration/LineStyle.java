package com.wk.chart.enumeration;

/**
 * <p>LineStyle</p>
 * 线条样式
 */

public enum LineStyle {
    NONE(0),//无(不显示)
    DOTTED(1),//虚线
    SOLID(2),//实线
    SCALE_OUTSIDE(3),//刻度线（外向）
    SCALE_INSIDE(4);//刻度线 (内向)

    LineStyle(int nativeInt) {
        this.nativeInt = nativeInt;
    }

    final int nativeInt;
}
