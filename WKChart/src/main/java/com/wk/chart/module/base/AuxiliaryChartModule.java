package com.wk.chart.module.base;

import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleType;

/**
 * <p>AuxiliaryChartModule</p>
 * 副图基类
 */

public abstract class AuxiliaryChartModule<T extends AbsEntry> extends AbsChartModule<T> {
    private boolean separateState = false;

    public boolean isSeparateState() {
        return separateState;
    }

    public void setSeparateState(boolean separateState) {
        this.separateState = separateState;
    }

    public AuxiliaryChartModule(ModuleType moduleType) {
        super(moduleType);
    }

}
