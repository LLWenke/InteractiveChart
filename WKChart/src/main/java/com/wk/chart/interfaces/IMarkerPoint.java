package com.wk.chart.interfaces;

import androidx.annotation.NonNull;

import com.wk.chart.entry.CandleEntry;


/**
 * <p>IMarkerPoint</p>
 * 标记点绘制组件接口
 */
public interface IMarkerPoint {
    float getHighPoint(@NonNull CandleEntry entry);

    float getLowPoint(@NonNull CandleEntry entry);

    int getMarkerPointCount();
}
