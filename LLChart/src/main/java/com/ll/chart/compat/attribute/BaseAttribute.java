
package com.ll.chart.compat.attribute;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import com.ll.chart.enumeration.AxisLabelLocation;
import com.ll.chart.enumeration.GridMarkerAlign;
import com.ll.chart.enumeration.AxisMarkerAlign;

/**
 * <p>视图属性配置基类</p>
 */

public class BaseAttribute {
  public Context context;

  public BaseAttribute(@NonNull Context context) {
    this.context = context;
  }

  /**
   * 各个视图模块的配置信息有关属性
   */
  public float candleViewHeight = 0; // K线模块高度
  public float volumeViewHeight = 0; // 交易量模块高度
  public float otherViewHeight = 0; // 副图模块高度
  public float timeLineViewHeight = 0; // 分时图模块高度
  public float depthViewHeight = 0; // 深度图图模块高度
  public float viewInterval = 30; // 各个视图模块间的间隔
  public float borderWidth = 3f; // 边框线条宽度
  public int borderColor = 0x1Affffff; // 边框线条颜色
  public float leftScrollOffset = 0;//X 轴方向的最小滚动值固定偏移量（左边）
  public float rightScrollOffset = 0;//X 轴方向的最大滚动值固定偏移量（右边）

  /**
   * 共用的有关属性
   */
  public float lineWidth = 3f; //线条宽度
  public int lineColor = 0x1Affffff; // 线条颜色
  public float labelSize = 26; // 标签字符大小
  public int labelColor = 0xff8c99a6; //标签字符颜色

  /**
   * 与 grid 标尺刻度有关属性
   */
  public int gridCount = 4; // grid数量
  public float gridLabelMarginTop = 0; // grid标签上边距
  public float gridLabelMarginBottom = 0; // grid标签下边距
  public boolean gridIsHide = false;//grid 是否显示
  public float gridMarkLineLength = 12f; //  grid刻度线长度

  /**
   * 与 axis 标尺刻度有关属性
   */
  public int axisCount = 5; // axis数量
  public float axisLabelLRMargin = 16f;//axis标签左右Margin
  public float axisLabelTBMargin = 8f;//axis标签上下Margin
  public AxisLabelLocation axisLabelLocation = AxisLabelLocation.LEFT; // axis标签显示位置

  /**
   * 与高亮线有关的属性
   */
  public boolean xHighlightIsHide = false; // X高亮线条是否显示
  // X高亮线条是否启用自动宽度管理(启用后xHighlightWidth将失效，宽度将实时跟随candleWidth)
  public boolean xHighlightAutoWidth = false;
  public int xHighlightColor = 0x4Dd8d8d8; // X高亮线条颜色 0xff1c232e

  public boolean yHighlightIsHide = false; // Y高亮线条是否显示
  // Y高亮线条是否启用自动宽度管理(启用后yHighlightWidth将失效，宽度将实时跟随candleWidth)
  public boolean yHighlightAutoWidth = false;
  public int yHighlightColor = 0xffffffff; // Y高亮线条颜色 0xff1c232e

  /**
   * 与MarkerView 有关的属性
   */
  public float markerBorderWidth = 3f; // MarkerView 边框宽度
  public float markerBorderRadius = 0; // MarkerView 边框圆角
  public float markerBorderTBPadding = 5f; // MarkerView 上下padding
  public float markerBorderLRPadding = 10f; // MarkerView 左右padding
  public int markerBorderColor = 0xffffffff; // MarkerView 边框颜色
  public float markerTextSize = 26; // MarkerView 字符大小
  public int markerTextColor = 0xff202a33; // MarkerView 字符颜色
  public Paint.Style markerStyle = Paint.Style.STROKE; //  MarkerView 的style（边框/边框和填充）
  public AxisMarkerAlign xMarkerAlign = AxisMarkerAlign.AUTO; // X 轴 MarkerView 对齐方向
  public GridMarkerAlign yMarkerAlign = GridMarkerAlign.AUTO; // Y 轴 MarkerView 对齐方向

  /**
   * 与选择器有关的属性
   */
  public float selectorPadding = 16;//信息选择框的padding
  public float selectorMarginX = 16;//信息选择框的左右margin
  public float selectorMarginY = 40;//信息选择框的上下margin
  public float selectorIntervalY = 16;//信息选择框的item上下间隔
  public float selectorIntervalX = 50;//信息选择框的item左右间隔
  public float selectorRadius = 5f;//信息选择框的圆角度数
  public float selectorBorderWidth = 3f;//选择器边框线宽度
  public int selectorBorderColor = 0xff394551;//选择器边框线颜色
  public int selectorBackgroundColor = 0xE6182027;//选择器背景颜色
  public int selectorLabelColor = 0xff8c99a6;//选择器label颜色
  public int selectorValueColor = 0xffffffff;//选择器value颜色
  public float selectorLabelSize = 26;//选择器label文字大小
  public float selectorValueSize = 26;//选择器value文字大小

  /**
   * 与指标文字有关的属性
   */
  public float indicatorsTextSize = 26;//指标文字大小
  public float indicatorsTextMarginX = 16;//指标文字左右margin
  public float indicatorsTextMarginY = 8;//指标文字上下margin
  public float indicatorsTextInterval = 16;//指标文字的间隔

  /**
   * 与游标指示器有关的属性
   */
  public float cursorBorderWidth = 3;//游标文字容器边框宽度
  public int cursorBackgroundColor = 0xFF202a33;//游标文字容器背景颜色
  public float cursorRadius = 5;//游标文字容器圆角

  /**
   * 涨跌有关的属性
   */
  public int increasingColor = 0xffb5fc00; // 上涨颜色
  public int decreasingColor = 0xfffe0d5e; // 下跌颜色
  public Paint.Style increasingStyle = Paint.Style.FILL; // 上涨蜡烛图填充样式。默认实心
  public Paint.Style decreasingStyle = Paint.Style.STROKE; // 下跌蜡烛图填充样式，默认空心

  /**
   * 缩放有关的属性
   */
  public float visibleCount = 50;
  // 竖屏状态下的默认缩放倍数下显示多少个蜡烛图。注：横屏时会自动根据视图宽高变化比例计算，不需要手工设置
  public float maxScale = 4;// 最多放大倍数
  public float minScale = 6;// 最多缩小倍数(缩小后的宽度不会小于 蜡烛图矩形边框线宽度（candleBorderWidth）)
  public float currentScale = 1;// 当前X轴缩放倍数

  /**
   * 极值有关属性
   */
  public float candleExtremumLabelSize = 26; // 蜡烛图极值字符大小
  public int candleExtremumLableColor = 0xa3ffffff; // 蜡烛图极值字符颜色

  /**
   * 与股票指标有关的属性
   */
  public int centerLineColor = 0xffffffff; // 视图中心线颜色

  public int ma5Color = 0xff9561fa; // MA5 平均线颜色
  public int ma10Color = 0xffffcb62; // MA10 平均线颜色
  public int ma20Color = 0xff34a9ff; // MA20 平均线颜色

  public int bollMidLineColor = 0xff82b1ff; // BOLL MID 线条颜色
  public int bollUpperLineColor = 0xffffab40; // BOLL UPPER 线条颜色
  public int bollLowerLineColor = 0xfff06292; // BOLL LOWER 线条颜色

  public int kdjKLineColor = 0xff82b1ff; // KDJ K 线条颜色
  public int kdjDLineColor = 0xffffab40; // KDJ D 线条颜色
  public int kdjJLineColor = 0xfff06292; // KDJ J 线条颜色

  public int deaLineColor = 0xff82b1ff; // DEA 线条颜色
  public int diffLineColor = 0xffffab40; // DIFF 线条颜色

  public int rsi1LineColor = 0xff82b1ff; // RSI 第一条线颜色
  public int rsi2LineColor = 0xffffab40; // RSI 第二条线颜色
  public int rsi3LineColor = 0xfff06292; // RSI 第三条线颜色

  /**
   * 与loading和error有关的属性
   */
  public float loadingTextSize = 26;
  public int loadingTextColor = 0xffffffff;
  public String loadingText = "Loading...";

  public float errorTextSize = 26;
  public int errorTextColor = 0xffffffff;
  public String errorText = "Empty";
}