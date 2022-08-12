package com.wk.chart.enumeration;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 模块类型
 */
@IntDef({
        ModuleType.MUTATION,
        ModuleType.FLOAT,
        ModuleType.CANDLE,
        ModuleType.TIME,
        ModuleType.DEPTH,
        ModuleType.VOLUME
})
@Retention(RetentionPolicy.SOURCE)
public @interface ModuleType {
    int MUTATION = 0;//突变模块（未指定主类型）

    int FLOAT = 1;//浮动/跨区域 指标

    int CANDLE = 2;//蜡烛图 指标

    int TIME = 3;//分时图 指标

    int DEPTH = 4;//深度图 指标

    int VOLUME = 5;//交易量 指标
}



