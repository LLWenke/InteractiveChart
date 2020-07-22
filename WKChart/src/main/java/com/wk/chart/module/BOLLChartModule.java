
package com.wk.chart.module;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndicatorType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AuxiliaryChartModule;

/**
 * <p>BOLL 组件</p>
 */

public class BOLLChartModule extends AuxiliaryChartModule<CandleEntry> {

    public BOLLChartModule() {
        super(ModuleType.BOLL);
        setIndicatorType(IndicatorType.BOLL);
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
            setMinY(item);
            //计算最大值
            setMaxY(item);
        }
    }
}
