
package com.wk.chart.module;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.IndexModule;

/**
 * <p>蜡烛图的指标模块</p>
 */

public class CandleIndexModule extends IndexModule<CandleEntry> {

    public CandleIndexModule() {
        super(ModuleType.MUTATION);
    }

    @Override
    public void computeMinMax(CandleEntry entry) {
        computeMinMax(entry.getIndex(getAttachIndexType()));
        computeMinMax(entry.getLineIndex(getAttachIndexType()));
    }

    private void computeMinMax(ValueEntry[] values) {
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
