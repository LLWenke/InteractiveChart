package com.wk.chart.stock.base;

import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ChartLevel;
import com.wk.chart.enumeration.ModuleType;

/**
 * <p>MainChartModule</p>
 *
 * 主图基类
 */

public abstract class MainChartModule<T extends AbsEntry> extends AbsChartModule<T> {

  public MainChartModule(ModuleType moduleType) {
    super(moduleType, ChartLevel.MAIN);
  }
}
