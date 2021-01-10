package com.wk.chart.module;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AuxiliaryModule;

/**
 * <p>交易量组件</p>
 */

public class VolumeModule extends AuxiliaryModule<CandleEntry> {

    public VolumeModule() {
        super(ModuleType.VOLUME);
        setAttachIndexType(IndexType.VOLUME_MA);
    }

    @Override
    public void computeMinMax(CandleEntry entry) {
        //计算最小值
        setMinY(entry.getVolume());
        //计算最大值
        setMaxY(entry.getVolume());
        ValueEntry[] values = entry.getLineIndex(getAttachIndexType());
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
