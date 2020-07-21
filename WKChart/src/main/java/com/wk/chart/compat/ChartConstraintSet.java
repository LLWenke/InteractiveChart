package com.wk.chart.compat;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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
