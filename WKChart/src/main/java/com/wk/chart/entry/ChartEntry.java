package com.wk.chart.entry;

import com.wk.chart.ChartView;
import com.wk.chart.handler.InteractiveHandler;

public class ChartEntry {
  private ChartView chart;
  private InteractiveHandler handler;

  public ChartEntry() {
  }

  public void setChart(ChartView chart) {
    this.chart = chart;
  }

  public ChartView getChart() {
    return chart;
  }

  public InteractiveHandler getHandler() {
    return handler;
  }

  public void setHandler(InteractiveHandler handler) {
    this.handler = handler;
  }
}
