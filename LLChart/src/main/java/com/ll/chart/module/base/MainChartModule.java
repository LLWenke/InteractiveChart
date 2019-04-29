package com.ll.chart.module.base;

import com.ll.chart.entry.AbsEntry;
import com.ll.chart.enumeration.ModuleType;

/**
 * <p>MainChartModule</p>
 *
 * 主图基类
 */

public abstract class MainChartModule<T extends AbsEntry> extends AbsChartModule<T> {

  public MainChartModule(ModuleType moduleType) {
    super(moduleType);
  }

}
