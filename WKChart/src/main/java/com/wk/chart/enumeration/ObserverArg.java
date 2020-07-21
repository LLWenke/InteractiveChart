package com.wk.chart.enumeration;

/**
 * 通知类型
 */
public enum ObserverArg {
    NORMAL(0),//无改变

    ADD(1),//添加

    PUSH(2),//推送

    INIT(3),//初始化

    REFRESH(4),//刷新

    CONFIG_CHANGE(5);//配置更改

    ObserverArg(int nativeInt) {
        this.nativeInt = nativeInt;
    }

    final int nativeInt;

    public static ObserverArg getObserverArg(int value) {
        for (ObserverArg item : values()) {
            if (item.ordinal() == value) {
                return item;
            }
        }
        return NORMAL;
    }

}
