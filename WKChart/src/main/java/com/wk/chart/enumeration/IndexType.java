package com.wk.chart.enumeration;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 指标类型
 */
@IntDef({
        IndexType.NONE,
        IndexType.MACD,
        IndexType.KDJ,
        IndexType.RSI,
        IndexType.BOLL,
        IndexType.CANDLE_MA,
        IndexType.VOLUME_MA,
        IndexType.EMA,
        IndexType.DMI,
        IndexType.WR,
        IndexType.SAR
})
@Retention(RetentionPolicy.SOURCE)
public @interface IndexType {
    int NONE = -1;//无指标

    int MACD = 5;//MACD 指标

    int KDJ = 6;//KDJ 指标

    int RSI = 7;//RSI 指标

    int BOLL = 8;//BOLL 指标

    int CANDLE_MA = 9;//蜡烛图平均线 指标

    int VOLUME_MA = 10;//交易量平均线 指标

    int EMA = 11;//EMA 指标

    int DMI = 12;//DMI 指标

    int WR = 13; //WR 指标

    int SAR = 14; //SAR 指标
}



