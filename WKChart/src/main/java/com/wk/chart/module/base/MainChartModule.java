package com.wk.chart.module.base;

import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleType;

/**
 * <p>MainChartModule</p>
 * 主图基类
 */

public abstract class MainChartModule<T extends AbsEntry> extends AbsChartModule<T> {

    public MainChartModule(ModuleType moduleType) {
        super(moduleType);
    }

    protected void calculationMarkerPointsCount(T entry) {
    }
}
