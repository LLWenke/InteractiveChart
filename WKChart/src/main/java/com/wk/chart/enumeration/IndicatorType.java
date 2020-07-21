package com.wk.chart.enumeration;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 指标类型
 */
@IntDef({
        IndicatorType.MACD,
        IndicatorType.KDJ,
        IndicatorType.RSI,
        IndicatorType.BOLL,
        IndicatorType.CANDLE_MA,
        IndicatorType.VOLUME_MA,
        IndicatorType.EMA,
        IndicatorType.DMI,
        IndicatorType.WR
})
@Retention(RetentionPolicy.SOURCE)
public @interface IndicatorType {
    int MACD = 0;//MACD 指标

    int KDJ = 1;//KDJ 指标

    int RSI = 2;//RSI 指标

    int BOLL = 3;//BOLL 指标

    int CANDLE_MA = 4;//蜡烛图平均线 指标

    int VOLUME_MA = 5;//交易量平均线 指标

    int EMA = 6;//EMA 指标

    int DMI = 7;//DMI 指标

    int WR = 8; //WR 指标
}



