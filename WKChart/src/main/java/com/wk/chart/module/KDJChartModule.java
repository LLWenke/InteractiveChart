
package com.wk.chart.module;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndicatorType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AuxiliaryChartModule;

/**
 * <p>KDJ 组件</p>
 */

public class KDJChartModule extends AuxiliaryChartModule<CandleEntry> {

    public KDJChartModule() {
        super(ModuleType.KDJ);
        setIndicatorType(IndicatorType.KDJ);
    }

    @Override
    public void computeMinMax(CandleEntry entry) {
        ValueEntry[] values = entry.getIndicator(getIndicatorType());
        if (null == values) {
            return;
        }
        for (ValueEntry item : values) {
            if (null == item) {
                continue;
            }
            //计算最小值
            if (item.value < getMinY().value) {
                setMinY(item);
            }
            //计算最大值
            if (item.value > getMaxY().value) {
                setMaxY(item);
            }
        }
    }
}
