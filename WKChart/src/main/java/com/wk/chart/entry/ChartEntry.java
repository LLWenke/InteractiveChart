package com.wk.chart.entry;

import com.wk.chart.Chart;
import com.wk.chart.handler.InteractiveHandler;

public class ChartEntry {
  private Chart chart;
  private InteractiveHandler handler;

  public ChartEntry() {
  }

  public void setChart(Chart chart) {
    this.chart = chart;
  }

  public Chart getChart() {
    return chart;
  }

  public InteractiveHandler getHandler() {
    return handler;
  }

  public void setHandler(InteractiveHandler handler) {
    this.handler = handler;
  }
}
