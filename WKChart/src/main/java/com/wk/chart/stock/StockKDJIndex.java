

package com.wk.chart.stock;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.stock.base.AuxiliaryChartModule;

/**
 * <p>KDJ 组件</p>
 */

public class StockKDJIndex extends AuxiliaryChartModule<CandleEntry> {

  public StockKDJIndex(ModuleType stockType) {
    super(stockType);
  }

  @Override
  public void computeMinMax(int currentIndex, CandleEntry entry) {
    if (entry.getK().value < getMinY().value) {
      setMinY(entry.getK());
    }
    if (entry.getD().value < getMinY().value) {
      setMinY(entry.getD());
    }
    if (entry.getJ().value < getMinY().value) {
      setMinY(entry.getJ());
    }

    if (entry.getK().value > getMaxY().value) {
      setMaxY(entry.getK());
    }
    if (entry.getD().value > getMaxY().value) {
      setMaxY(entry.getD());
    }
    if (entry.getJ().value > getMaxY().value) {
      setMaxY(entry.getJ());
    }
  }
}
