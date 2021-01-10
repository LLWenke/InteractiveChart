package com.wk.chart.enumeration;

/**
 * <p>GridLineStyle</p>
 * Grid线样式
 */

public enum GridLineStyle {
    NONE(0),//虚线
    LINE(1),//横线
    GRADUATION(2),//刻度（内向）
    GRADUATION_INSIDE(3);//刻度 (外向)

    GridLineStyle(int nativeInt) {
        this.nativeInt = nativeInt;
    }

    final int nativeInt;
}
