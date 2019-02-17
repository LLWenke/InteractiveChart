

package com.wk.chart.stock;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.stock.base.AuxiliaryChartModule;

/**
 * <p>MACD 组件</p>
 */

public class StockMACDIndex extends AuxiliaryChartModule<CandleEntry> {

  public StockMACDIndex(ModuleType stockType) {
    super(stockType);
  }

  @Override
  public void computeMinMax(int currentIndex, CandleEntry entry) {
    if (entry.getMacd().value < getMinY().value) {
      setMinY(entry.getMacd());
    }
    if (entry.getDea().value < getMinY().value) {
      setMinY(entry.getDea());
    }
    if (entry.getDiff().value < getMinY().value) {
      setMinY(entry.getDiff());
    }

    if (entry.getMacd().value > getMaxY().value) {
      setMaxY(entry.getMacd());
    }
    if (entry.getDea().value > getMaxY().value) {
      setMaxY(entry.getDea());
    }
    if (entry.getDiff().value > getMaxY().value) {
      setMaxY(entry.getDiff());
    }
  }
}
