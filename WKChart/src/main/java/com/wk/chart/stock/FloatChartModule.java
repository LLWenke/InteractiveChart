package com.wk.chart.stock;

import android.graphics.RectF;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.stock.base.AuxiliaryChartModule;

/**
 * <p>浮动组件</p>
 */

public class FloatChartModule extends AuxiliaryChartModule<AbsEntry> {

  public FloatChartModule(RectF rect) {
    super(ModuleType.FLOAT);
    setRect(rect);
  }

  @Override
  public void computeMinMax(int currentIndex, AbsEntry entry) {
  }
}
