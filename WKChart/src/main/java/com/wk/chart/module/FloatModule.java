package com.wk.chart.module;

import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.ModuleGroup;

/**
 * <p>浮动模块</p>
 */

public class FloatModule extends AbsModule<AbsEntry> {

    public FloatModule() {
        super(ModuleGroup.FLOAT, IndexType.NONE);
    }

    @Override
    public void computeMinMax(AbsEntry entry) {
    }
}
