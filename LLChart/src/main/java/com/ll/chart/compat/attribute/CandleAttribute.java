
package com.ll.chart.compat.attribute;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * <p>蜡烛图属性配置类</p>
 */

public class CandleAttribute extends BaseAttribute {

  public CandleAttribute(@NonNull Context context) {
    super(context);
  }

  /**
   * 与蜡烛图有关的属性
   */
  public float candleSpace = 8f;//蜡烛图间隔
  public float candleWidth = 28f;//蜡烛图初始宽度（缩放都将以此宽度为基准）
  public float candleBorderWidth = 3f; // 蜡烛图矩形边框线宽度

  /**
   * 与分时图有关的属性
   */
  public float timeLineWidth = 3f; // 分时线宽度
  public int timeLineColor = 0xFF688FDB; // 分时线颜色
  public int timeLineShaderColorBegin = 0xA0688FDB; // 分时线颜色渐变起始色值
  public int timeLineShaderColorEnd = 0x20688FDB; // 分时线颜色渐变结束色值
}