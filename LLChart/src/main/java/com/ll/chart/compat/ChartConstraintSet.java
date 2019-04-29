package com.ll.chart.compat;

import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;

public class ChartConstraintSet extends ConstraintSet {

  public ChartConstraintSet() {
    super();
  }

  public ChartConstraintSet(ConstraintSet set) {
    clone(set);
  }

  public ChartConstraintSet(ConstraintLayout layout) {
    clone(layout);
  }
}
