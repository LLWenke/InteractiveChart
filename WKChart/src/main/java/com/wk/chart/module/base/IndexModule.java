
package com.wk.chart.module.base;

import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleGroupType;
import com.wk.chart.enumeration.ModuleType;

/**
 * <p>指标 模块</p>
 */

public abstract class IndexModule<T extends AbsEntry> extends AbsModule<T> {

    public IndexModule(@ModuleType int type) {
        super(type, ModuleGroupType.INDEX);
    }
}
