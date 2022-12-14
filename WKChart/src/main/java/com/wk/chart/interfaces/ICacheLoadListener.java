package com.wk.chart.interfaces;

import androidx.annotation.Nullable;

import com.wk.chart.entry.ChartCache;
import com.wk.chart.enumeration.TimeType;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface ICacheLoadListener {
    void onLoadCacheTypes(@Nullable TimeType timeType, boolean isNeedLoadData, @NotNull Map<Integer, ChartCache.TypeEntry> typeMap);
}
