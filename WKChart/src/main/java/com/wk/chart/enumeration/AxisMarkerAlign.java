package com.wk.chart.enumeration;

/**
 * <p>AxisMarkerAlign</p>
 */

public enum AxisMarkerAlign {
    LEFT_INSIDE(0),//靠左（内部）

    RIGHT_INSIDE(1),//靠右（内部）

    AUTO(2);//自动

    AxisMarkerAlign(int nativeInt) {
        this.nativeInt = nativeInt;
    }

    final int nativeInt;
}
