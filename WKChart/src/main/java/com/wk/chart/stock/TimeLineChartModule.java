package com.wk.chart.stock;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.stock.base.MainChartModule;

/**
 * <p>分时图组件</p>
 */

public class TimeLineChartModule extends MainChartModule<CandleEntry> {

  public TimeLineChartModule(ModuleType stockType) {
    super(stockType);
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
