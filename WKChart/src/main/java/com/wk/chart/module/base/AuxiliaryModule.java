package com.wk.chart.module.base;

import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;

/**
 * <p>AuxiliaryChartModule</p>
 * 副图基类
 */

public abstract class AuxiliaryModule<T extends AbsEntry> extends AbsModule<T> {

    public AuxiliaryModule(@ModuleType int type) {
        super(type, ModuleGroupType.AUXILIARY);
    }

}
