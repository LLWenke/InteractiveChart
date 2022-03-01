package com.wk.chart.enumeration;

/**
 * 通知类型
 */
public enum ObserverArg {
    NORMAL(0),//无改变

    INIT(1),//初始化

    ADD(2),//添加

    RESET(3),//重置

    UPDATE(4),//更新

    ATTR_UPDATE(5),//配置属性更新

    FORMAT_UPDATE(6);//格式更新

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
