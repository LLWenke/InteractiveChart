

package com.ll.chart.module;

import com.ll.chart.entry.CandleEntry;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.module.base.AuxiliaryChartModule;

/**
 * <p>MACD 组件</p>
 */

public class MACDChartModule extends AuxiliaryChartModule<CandleEntry> {

  public MACDChartModule() {
    super(ModuleType.MACD);
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
