package com.wk.chart.module;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndicatorType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AuxiliaryChartModule;

/**
 * <p>交易量组件</p>
 */

public class VolumeChartModule extends AuxiliaryChartModule<CandleEntry> {

    public VolumeChartModule() {
        super(ModuleType.VOLUME);
        setIndicatorType(IndicatorType.VOLUME_MA);
    }

    @Override
    public void computeMinMax(CandleEntry entry) {
        //计算最小值
        setMinY(entry.getVolume());
        //计算最大值
        setMaxY(entry.getVolume());
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
