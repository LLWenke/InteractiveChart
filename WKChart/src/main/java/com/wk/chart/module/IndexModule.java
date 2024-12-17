
package com.wk.chart.module;

import com.wk.chart.entry.IndexEntry;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.IndexType;
import com.wk.chart.enumeration.ModuleGroup;

/**
 * <p>指标图模块</p>
 */

public class IndexModule extends AbsModule<IndexEntry> {

    public IndexModule(@IndexType int moduleIndexType) {
        super(ModuleGroup.INDEX, moduleIndexType);
    }

    @Override
    public void computeMinMax(IndexEntry entry) {
        computeMinMax(entry.getIndex(getModuleIndexType()));
        computeMinMax(entry.getLineIndex(getModuleIndexType()));
        for (Integer index : getAttachIndexSet()) {
            computeMinMax(entry.getIndex(index));
            computeMinMax(entry.getLineIndex(index));
        }
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
