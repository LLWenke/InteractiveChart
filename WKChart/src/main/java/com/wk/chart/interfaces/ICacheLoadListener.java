package com.wk.chart.interfaces;

import com.wk.chart.entry.ChartCache;
import com.wk.chart.enumeration.TimeType;

import java.util.List;

public interface ICacheLoadListener {
    void onLoadCacheTypes(TimeType timeType, boolean isLoadData, List<ChartCache.TypeEntry> types);
}
