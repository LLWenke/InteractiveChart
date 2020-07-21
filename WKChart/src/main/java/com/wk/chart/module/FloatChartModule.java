package com.wk.chart.module;

import android.graphics.RectF;

import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AuxiliaryChartModule;

/**
 * <p>浮动组件</p>
 */

public class FloatChartModule extends AuxiliaryChartModule<AbsEntry> {

    public FloatChartModule(RectF rect) {
        super(ModuleType.FLOAT);
        setRect(rect);
    }

    @Override
    public void computeMinMax(AbsEntry entry) {
    }
}
