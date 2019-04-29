
package com.ll.chart.module;

import com.ll.chart.entry.CandleEntry;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.module.base.AuxiliaryChartModule;

/**
 * <p>BOLL 组件</p>
 */

public class BOLLChartModule extends AuxiliaryChartModule<CandleEntry> {

  public BOLLChartModule() {
    super(ModuleType.BOLL);
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
