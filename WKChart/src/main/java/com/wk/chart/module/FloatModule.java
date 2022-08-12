package com.wk.chart.module;

import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AbsModule;

/**
 * <p>浮动模块</p>
 */

public class FloatModule extends AbsModule<AbsEntry> {

    public FloatModule() {
        super(ModuleType.FLOAT, ModuleGroupType.FLOAT);
        setEnable(true);
    }

    @Override
    public void computeMinMax(AbsEntry entry) {
    }
}
