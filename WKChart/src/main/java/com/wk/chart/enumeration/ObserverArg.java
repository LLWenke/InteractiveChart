package com.wk.chart.enumeration;

/**
 * 通知类型
 */
public enum ObserverArg {
    NORMAL(0),//无改变

    ADD(1),//添加

    PUSH(2),//推送

    REFRESH(3),//刷新

    INIT(4),//初始化

    CLEAR(5),//清空

    ATTR_UPDATE(6);//配置属性更新

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
