package com.ll.chart.module;

import com.ll.chart.entry.CandleEntry;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.module.base.MainChartModule;

/**
 * <p>分时图组件</p>
 */

public class TimeLineChartModule extends MainChartModule<CandleEntry> {

  public TimeLineChartModule() {
    super(ModuleType.TIME);
  }

  @Override
  public void computeMinMax(int currentIndex, CandleEntry entry) {
    //计算最小值
    if (entry.getClose().value < getMinY().value) {
      setMinY(entry.getClose());
    }
    //计算最大值
    if (entry.getClose().value > getMaxY().value) {
      setMaxY(entry.getClose());
    }
  }
}
