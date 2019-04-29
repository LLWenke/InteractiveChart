package com.ll.chart.module;

import android.graphics.RectF;
import com.ll.chart.entry.AbsEntry;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.module.base.AuxiliaryChartModule;

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
