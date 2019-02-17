package com.wk.chart.stock;

import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.stock.base.AuxiliaryChartModule;

/**
 * <p>交易量组件</p>
 */

public class VolumeChartModule extends AuxiliaryChartModule<CandleEntry> {

  public VolumeChartModule(ModuleType stockType) {
    super(stockType);
  }

  @Override
  public void computeMinMax(int currentIndex, CandleEntry entry) {
    if (entry.getVolume().value < getMinY().value) {
      setMinY(entry.getVolume());
    }
    if (entry.getVolumeMa5().value < getMinY().value) {
      setMinY(entry.getVolumeMa5());
    }
    if (entry.getVolumeMa10().value < getMinY().value) {
      setMinY(entry.getVolumeMa10());
    }

    if (entry.getVolume().value > getMaxY().value) {
      setMaxY(entry.getVolume());
    }
    if (entry.getVolumeMa5().value > getMaxY().value) {
      setMaxY(entry.getVolumeMa5());
    }
    if (entry.getVolumeMa10().value > getMaxY().value) {
      setMaxY(entry.getVolumeMa10());
    }
  }
}
