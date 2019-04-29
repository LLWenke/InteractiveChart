package com.ll.chart.module.base;

import com.ll.chart.entry.AbsEntry;
import com.ll.chart.enumeration.ModuleType;

/**
 * <p>AuxiliaryChartModule</p>
 * 副图基类
 */

public abstract class AuxiliaryChartModule<T extends AbsEntry> extends AbsChartModule<T> {

  public AuxiliaryChartModule(ModuleType moduleType) {
    super(moduleType);
  }
}
