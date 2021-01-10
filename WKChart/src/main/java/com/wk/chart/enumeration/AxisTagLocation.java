package com.wk.chart.enumeration;

/**
 * <p>AxisTagLocation</p>
 */

public enum AxisTagLocation {
    LEFT(0),//左边（外部）

    LEFT_INSIDE(1),//左边（内部）

    RIGHT(2),//右边（外部）

    RIGHT_INSIDE(3),//右边（内部）

    ALL(4),//左右都有（外部）

    ALL_INSIDE(5);//左右都有（内部）

    AxisTagLocation(int nativeInt) {
        this.nativeInt = nativeInt;
    }

    final int nativeInt;
}
