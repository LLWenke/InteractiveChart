
package com.wk.chart.stock;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.stock.base.AuxiliaryChartModule;

/**
 * <p>BOLL 组件</p>
 */

public class BOLLChartModule extends AuxiliaryChartModule<CandleEntry> {

  public BOLLChartModule(ModuleType stockType) {
    super(stockType);
  }

  @Override
  public void computeMinMax(int currentIndex, CandleEntry entry) {
    if (entry.getMb().value < getMinY().value) {
      setMinY(entry.getMb());
    }

    if (entry.getMb().value > getMaxY().value) {
      setMaxY(entry.getMb());
    }

    if (currentIndex > 0) {
      if (entry.getUp().value < getMinY().value) {
        setMinY(entry.getUp());
      }
      if (entry.getDn().value < getMinY().value) {
        setMinY(entry.getDn());
      }

      if (entry.getUp().value > getMaxY().value) {
        setMaxY(entry.getUp());
      }
      if (entry.getDn().value > getMaxY().value) {
        setMaxY(entry.getDn());
      }
    }
  }
}
