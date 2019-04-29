

package com.ll.chart.module;

import com.ll.chart.entry.CandleEntry;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.module.base.AuxiliaryChartModule;

/**
 * <p>KDJ 组件</p>
 */

public class KDJChartModule extends AuxiliaryChartModule<CandleEntry> {

  public KDJChartModule() {
    super(ModuleType.KDJ);
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
