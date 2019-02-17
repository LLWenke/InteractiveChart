package com.wk.chart.stock.base;

import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ChartLevel;
import com.wk.chart.enumeration.ModuleType;

/**
 * <p>AuxiliaryChartModule</p>
 * 副图基类
 */

public abstract class AuxiliaryChartModule<T extends AbsEntry> extends AbsChartModule<T> {

  public AuxiliaryChartModule(ModuleType moduleType) {
    super(moduleType, ChartLevel.AUXILIARY);
  }
}
