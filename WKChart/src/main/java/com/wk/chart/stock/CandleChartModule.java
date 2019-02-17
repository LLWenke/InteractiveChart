package com.wk.chart.stock;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.stock.base.MainChartModule;

/**
 * <p>蜡烛图组件</p>
 */

public class CandleChartModule extends MainChartModule<CandleEntry> {

  public CandleChartModule(ModuleType stockType) {
    super(stockType);
  }

  @Override
  public void computeMinMax(int currentIndex, CandleEntry entry) {
    //计算最小值
    if (entry.getLow().value < getMinY().value) {
      setMinY(entry.getLow());
    }
    if (entry.getMa5().value < getMinY().value) {
      setMinY(entry.getMa5());
    }
    if (entry.getMa10().value < getMinY().value) {
      setMinY(entry.getMa10());
    }
    if (entry.getMa20().value < getMinY().value) {
      setMinY(entry.getMa20());
    }
    //计算最大值
    if (entry.getHigh().value > getMaxY().value) {
      setMaxY(entry.getHigh());
    }
    if (entry.getMa5().value > getMaxY().value) {
      setMaxY(entry.getMa5());
    }
    if (entry.getMa10().value > getMaxY().value) {
      setMaxY(entry.getMa10());
    }
    if (entry.getMa20().value > getMaxY().value) {
      setMaxY(entry.getMa20());
    }
  }
}
