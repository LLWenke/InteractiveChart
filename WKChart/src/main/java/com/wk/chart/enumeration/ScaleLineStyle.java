package com.wk.chart.enumeration;

/**
 * <p>ScaleLineStyle</p>
 * 刻度线样式
 */

public enum ScaleLineStyle {
    NONE(0),//无(不显示)
    DOTTED(1),//虚线
    SOLID(2),//实线
    SHORT_OUTSIDE(3),//短线（外向）
    SHORT_INSIDE(4);//短线 (内向)

    ScaleLineStyle(int nativeInt) {
        this.nativeInt = nativeInt;
    }

    final int nativeInt;
}
