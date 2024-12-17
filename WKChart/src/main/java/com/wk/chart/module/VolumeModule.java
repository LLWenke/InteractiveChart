package com.wk.chart.module;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.ModuleGroup;

/**
 * <p>交易量模块</p>
 */

public class VolumeModule extends AbsModule<CandleEntry> {

    public VolumeModule() {
        super(ModuleGroup.INDEX, IndexType.VOLUME);
    }

    @Override
    public void computeMinMax(CandleEntry entry) {
        //计算最小值
        setMinY(entry.getVolume());
        //计算最大值
        setMaxY(entry.getVolume());
        //计算指标最大最小值
        for (Integer index : getAttachIndexSet()) {
            computeIndexMinMax(entry.getIndex(index));
            computeIndexMinMax(entry.getLineIndex(index));
        }
    }

    private void computeIndexMinMax(ValueEntry[] values) {
        if (null == values) return;
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
