package com.wk.chart.module;


import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndicatorType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AuxiliaryChartModule;

/**
 * <p>MACD 组件</p>
 */

public class MACDChartModule extends AuxiliaryChartModule<CandleEntry> {

    public MACDChartModule() {
        super(ModuleType.MACD);
        setIndicatorType(IndicatorType.MACD);
    }

    /**
     * MACD指标的minY不能大于0，否则会出现超出绘制区域的现象
     */
    @Override
    public ValueEntry getMinY() {
        ValueEntry min = super.getMinY();
        return min.result > 0f ? zeroEntry : min;
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
