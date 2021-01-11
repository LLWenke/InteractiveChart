package com.wk.chart.enumeration;

/**
 * 通知类型
 */
public enum ObserverArg {
    NORMAL(0),//无改变

    ADD(1),//添加

    PUSH(2),//推送

    RESET(3),//重置

    REFRESH(4),//刷新

    INIT(5),//初始化

    INIT_AND_RESET(6),//初始化+重置

    RESET_RATE(7),//重置汇率

    CLEAR(8);//清空

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
