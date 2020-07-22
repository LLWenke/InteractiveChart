package com.wk.chart.module;

import com.wk.chart.entry.DepthEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.MainChartModule;

/**
 * <p>深度图组件</p>
 */

public class DepthChartModule extends MainChartModule<DepthEntry> {

    public DepthChartModule() {
        super(ModuleType.DEPTH);
    }

    @Override
    public void computeMinMax(DepthEntry entry) {
        //计算最小值
        setMinY(entry.getTotalAmount());
        setMinX(entry.getPrice());
        //计算最大值
        setMaxY(entry.getTotalAmount());
        setMaxX(entry.getPrice());
    }
}
