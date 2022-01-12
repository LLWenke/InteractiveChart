package com.wk.chart.interfaces;

import androidx.annotation.Nullable;

import com.wk.chart.entry.ChartCache;
import com.wk.chart.enumeration.TimeType;

import java.util.HashMap;

public interface ICacheLoadListener {
    void onLoadCacheTypes(@Nullable TimeType timeType, boolean isNeedLoadData, HashMap<Integer, ChartCache.TypeEntry> typeMap);
}
