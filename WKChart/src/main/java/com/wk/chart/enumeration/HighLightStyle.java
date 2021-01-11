package com.wk.chart.enumeration;

/**
 * <p>HighLightStyle</p>
 * 高亮线样式
 */

public enum HighLightStyle {
    SOLID(0),//实线

    DOTTED(1);//虚线

    HighLightStyle(int nativeInt) {
        this.nativeInt = nativeInt;
    }

    final int nativeInt;
}
