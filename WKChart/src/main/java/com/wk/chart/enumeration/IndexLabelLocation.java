package com.wk.chart.enumeration;

public enum IndexLabelLocation {

    LEFT_TOP_INSIDE(0),//左上（内部）

    RIGHT_TOP_INSIDE(1),//右上（内部）

    LEFT_TOP(2),//左上

    RIGHT_TOP(3),//右上

    LEFT_BOTTOM_INSIDE(4),//左下（内部）

    RIGHT_BOTTOM_INSIDE(5),//右下（内部）

    LEFT_BOTTOM(6),//左下

    RIGHT_BOTTOM(7);//右下

    IndexLabelLocation(int nativeInt) {
        this.nativeInt = nativeInt;
    }

    final int nativeInt;
}
