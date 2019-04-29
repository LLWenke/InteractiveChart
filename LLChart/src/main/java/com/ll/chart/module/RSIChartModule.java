

package com.ll.chart.module;

import com.ll.chart.entry.CandleEntry;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.module.base.AuxiliaryChartModule;

/**
 * <p>RSI 组件</p>
 */

public class RSIChartModule extends AuxiliaryChartModule<CandleEntry> {

  public RSIChartModule() {
    super(ModuleType.RSI);
  }

  @Override
  public void computeMinMax(int currentIndex, CandleEntry entry) {
    if (entry.getRsi1().value < getMinY().value) {
      setMinY(entry.getRsi1());
    }
    if (entry.getRsi2().value < getMinY().value) {
      setMinY(entry.getRsi2());
    }
    if (entry.getRsi3().value < getMinY().value) {
      setMinY(entry.getRsi3());
    }

    if (entry.getRsi1().value > getMaxY().value) {
      setMaxY(entry.getRsi1());
    }
    if (entry.getRsi2().value > getMaxY().value) {
      setMaxY(entry.getRsi2());
    }
    if (entry.getRsi3().value > getMaxY().value) {
      setMaxY(entry.getRsi3());
    }
  }
}
