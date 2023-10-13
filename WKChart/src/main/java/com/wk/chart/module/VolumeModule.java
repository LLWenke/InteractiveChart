package com.wk.chart.module;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AuxiliaryModule;

/**
 * <p>交易量模块</p>
 */

public class VolumeModule extends AuxiliaryModule<CandleEntry> {

    public VolumeModule() {
        super(ModuleType.VOLUME);
    }

    @Override
    public void computeMinMax(CandleEntry entry) {
        //计算最小值
        setMinY(entry.getVolume());
        //计算最大值
        setMaxY(entry.getVolume());
        computeIndexMinMax(entry.getIndex(getAttachIndexType()));
        computeIndexMinMax(entry.getLineIndex(getAttachIndexType()));
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
