package com.wk.chart.compat;

import com.wk.chart.ChartView;
import com.wk.chart.entry.ChartEntry;

/**
 * 缓存
 */
public class CacheHolder {
  private static ChartView cacheChart = null;

  /**
   * 缓存图表
   */
  public static void cacheChart(ChartEntry chart) {
    cacheChart = chart.getChart();
  }

  /**
   * 获取缓存的图表
   */
  public static ChartView getCacheChart() {
    return cacheChart;
  }

  /**
   * 清空缓存图表
   */
  public static void clearCacheChart() {
    cacheChart = null;
  }

  /**
   * 图表是否缓存过
   */
  public static boolean isCache() {
    return null != cacheChart;
  }
}
