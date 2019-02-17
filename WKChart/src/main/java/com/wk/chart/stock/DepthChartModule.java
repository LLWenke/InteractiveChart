package com.wk.chart.stock;

import com.wk.chart.entry.DepthEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.stock.base.MainChartModule;

/**
 * <p>深度图组件</p>
 */

public class DepthChartModule extends MainChartModule<DepthEntry> {

  public DepthChartModule(ModuleType stockType) {
    super(stockType);
  }

  @Override
  public void computeMinMax(int currentIndex, DepthEntry entry) {
    //计算最小值
    if (entry.getTotalAmount().value < getMinY().value) {
      setMinY(entry.getTotalAmount());
    }
    if (entry.getPrice().value < getMinX().value) {
      setMinX(entry.getPrice());
    }
    //计算最大值
    if (entry.getTotalAmount().value > getMaxY().value) {
      setMaxY(entry.getTotalAmount());
    }
    if (entry.getPrice().value > getMaxX().value) {
      setMaxX(entry.getPrice());
    }
  }
}
