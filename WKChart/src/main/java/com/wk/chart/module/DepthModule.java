package com.wk.chart.module;

import com.wk.chart.entry.DepthEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.MainModule;

/**
 * <p>深度图模块</p>
 */

public class DepthModule extends MainModule<DepthEntry> {

    public DepthModule() {
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
