package com.wk.demo.model;

import com.wk.chart.enumeration.DisplayType;
import com.wk.chart.enumeration.ModuleType;
import java.util.List;

public class ChartCache {
  public List<ModuleType> enableModuleTypes;
  public DisplayType displayType = DisplayType.oneHour;
  public int beginPosition = 0;
  public float scale = 1;
}
