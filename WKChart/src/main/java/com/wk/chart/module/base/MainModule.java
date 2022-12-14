package com.wk.chart.module.base;

import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;

/**
 * <p>MainChartModule</p>
 * 主图基类
 */

public abstract class MainModule<T extends AbsEntry> extends AbsModule<T> {

    protected MainModule(@ModuleType int type) {
        super(type, ModuleGroupType.MAIN);
    }
}
