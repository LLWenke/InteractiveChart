package com.wk.chart.enumeration;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>MarkerPointType</p>
 * 标记点类型
 */
@IntDef({
        MarkerPointType.NORMAL,
        MarkerPointType.B,
        MarkerPointType.S,
        MarkerPointType.T
})
@Retention(RetentionPolicy.SOURCE)
public @interface MarkerPointType {
    int NORMAL = 0;//没有标记
    int B = 1;//买入存在时的标记
    int S = 2;//卖出存在时的标记
    int T = 3;//买入和卖出同时存在时的标记
}
