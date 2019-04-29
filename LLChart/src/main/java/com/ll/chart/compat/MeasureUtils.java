
package com.ll.chart.compat;

import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.util.Log;
import com.ll.chart.compat.attribute.BaseAttribute;
import com.ll.chart.entry.AbsEntry;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.module.base.AbsChartModule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <p>MeasureUtils</p>
 */

public class MeasureUtils {
  private static final String TAG = "MeasureUtils";
  private BaseAttribute attribute;
  private final List<AbsChartModule> chartModules = new ArrayList<>();//存储高度比例信息的集合
  private boolean isInitProportion = true;//是否初始化高度比例
  private float[] oldViewTBCoordinates;//用来储存每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...](old)
  private float[] newViewTBCoordinates;//用来储存每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...](new)
  private int enableViewCount = 0;//启用的view的数量
  private Rect rect = new Rect();//用于测量文字的实际占用区域

  public MeasureUtils(@NonNull BaseAttribute attribute) {
    this.attribute = attribute;
    TextPaint measurePaint = new TextPaint();
    measurePaint.setTextSize(attribute.labelSize);
    measurePaint.setTypeface(FontConfig.typeFace);
    Utils.measureTextArea(measurePaint, rect);
  }

  /**
   * 计算子View高度，并返回真实高度
   */
  public float childViewHeightMeasure(float height) {
    float viewBorderWidthCount = attribute.borderWidth * 2;
    float viewIntervalCount = attribute.gridLabelMarginTop
        + rect.height()
        + attribute.gridLabelMarginBottom;
    float proportion = 1f;
    this.enableViewCount = 0;
    if (isInitProportion) {
      Collections.sort(chartModules, new Comparator<AbsChartModule>() {
        public int compare(AbsChartModule arg0, AbsChartModule arg1) {
          return arg1.getViewHeight().compareTo(arg0.getViewHeight());
        }
      });
    }

    for (int i = 0, z = chartModules.size(); i < z; i++) {
      AbsChartModule item = chartModules.get(i);
      if (item.isEnable()) {
        enableViewCount++;
        viewIntervalCount += attribute.viewInterval + viewBorderWidthCount;
      }
    }
    //减去最后一个view的viewInterval+ borderWidth（也就是最后一个view没有间隔）
    viewIntervalCount -= attribute.viewInterval;
    height -= viewIntervalCount;
    float parentHeight = viewIntervalCount;
    float value;
    for (int i = 0, z = chartModules.size(), count = enableViewCount; i < z; i++) {
      AbsChartModule item = chartModules.get(i);
      if (isInitProportion) {
        if (item.getViewHeight() == 0 && proportion < 1) {
          value = proportion / (item.isEnable() ? --count : count);
        } else {
          value = item.getViewHeight() / height;
          proportion -= item.isEnable() ? value : 0;
        }
        item.setProportion(value == 0 ? getDefaultProportion(item.getModuleType()) : value);
      }
      item.setViewHeight(height * item.getProportion());
      parentHeight += item.isEnable() ? item.getViewHeight() : 0;
      //Log.e(TAG, "--Proportion：" + item.getProportion());
      Log.e(TAG, "--viewHeight：" + item.getViewHeight());
    }
    isInitProportion = false;
    return parentHeight;
  }

  /**
   * 初始化每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...]
   * 此方法只更新top和bottom(如：y0 y1)
   */
  public void initViewCoordinates() {
    int coordinatesCount = enableViewCount * 4;
    if (null == oldViewTBCoordinates || coordinatesCount != oldViewTBCoordinates.length) {
      oldViewTBCoordinates = new float[coordinatesCount];
      newViewTBCoordinates = new float[coordinatesCount];
    }
    for (int i = 0, z = chartModules.size(), point = -1; i < z; i++) {
      AbsChartModule item = chartModules.get(i);
      if (item.isEnable()) {
        point += 2;
        oldViewTBCoordinates[point] = item.getRect().top;
        point += 2;
        oldViewTBCoordinates[point] = item.getRect().bottom;
      }
    }
  }

  /**
   * 构建每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...]
   * 此方法只更新left和right(如：x0 x1)
   */
  public float[] buildViewLRCoordinates(float x0, float x1) {
    return buildViewLRCoordinates(x0, x1, 0, 0, null);
  }

  /**
   * 构建每个已经启用的View的坐标[x0 y0 x1 y1 x2 y2 ...]
   * 此方法只更新left和right(如：x0 x1)
   * 如果focusArea不为null的时候，会在focusArea所在的坐标上更新y0 y1 (即top 和 bottom)
   */
  public float[] buildViewLRCoordinates(float x0, float x1, float y0,
      float y1, RectF focusArea) {
    for (int i = 0; i < newViewTBCoordinates.length; i += 2) {
      newViewTBCoordinates[i] = x0;
      i += 2;
      newViewTBCoordinates[i] = x1;
      int topPosition = i - 1;
      int bottomPosition = i + 1;
      if (null != focusArea && focusArea.contains(oldViewTBCoordinates[i],
          oldViewTBCoordinates[topPosition])) {
        newViewTBCoordinates[topPosition] = y0;
        newViewTBCoordinates[bottomPosition] = y1;
      } else {
        newViewTBCoordinates[topPosition] = oldViewTBCoordinates[topPosition];
        newViewTBCoordinates[bottomPosition] = oldViewTBCoordinates[bottomPosition];
      }
    }
    return newViewTBCoordinates;
  }

  /**
   * 根据图表类型返回默认的高度比例
   */
  private float getDefaultProportion(ModuleType moduleType) {
    switch (moduleType) {
      case CANDLE:// K线 模块高度
      case TIME:// 分时图 模块高度
        return 0.55f;
      case VOLUME: // 交易量 模块高度
        return 0.15f;
      case MACD:  //MACD 模块高度
      case KDJ:  //KDJ 模块高度
      case RSI:  //RSI 模块高度
      case BOLL:  //BOLL 模块高度
        return 0.3f;
      case DEPTH:// 深度图 模块高度
      default:
        return 1f;
    }
  }

  /**
   * 更新视图集合
   */
  public void chartModuleNotifyDataSetChanged(List<AbsChartModule<? super AbsEntry>> modules) {
    chartModules.clear();
    chartModules.addAll(modules);
    isInitProportion = true;
  }
}
