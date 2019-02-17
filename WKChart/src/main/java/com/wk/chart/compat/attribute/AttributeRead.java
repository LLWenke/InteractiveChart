
package com.wk.chart.compat.attribute;

import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.TextUtils;
import com.wk.chart.R;
import com.wk.chart.enumeration.AxisMarkerAlign;
import com.wk.chart.enumeration.GridLabelLocation;
import com.wk.chart.enumeration.GridMarkerAlign;

/**
 * <p>AttributeRead</p>
 */

public class AttributeRead {

  /**
   * 初始化 BaseAttribute
   */
  public void initAttribute(TypedArray a, BaseAttribute attribute) {
    /**
     * 各个视图模块的配置信息有关属性
     */
    attribute.candleViewHeight = a.getDimension(R.styleable.ChartAttr_candleViewHeight,
        attribute.candleViewHeight);// K线模块高度

    attribute.volumeViewHeight = a.getDimension(R.styleable.ChartAttr_volumeViewHeight,
        attribute.volumeViewHeight);// 交易量模块高度

    attribute.otherViewHeight = a.getDimension(R.styleable.ChartAttr_otherViewHeight,
        attribute.otherViewHeight);// 副图模块高度

    attribute.timeLineViewHeight = a.getDimension(R.styleable.ChartAttr_timeLineViewHeight,
        attribute.timeLineViewHeight);// 分时图模块高度

    attribute.depthViewHeight = a.getDimension(R.styleable.ChartAttr_depthViewHeight,
        attribute.depthViewHeight);// 深度图模块高度

    attribute.viewInterval = a.getDimension(R.styleable.ChartAttr_viewInterval,
        attribute.viewInterval);// 各个视图模块间的间隔

    attribute.borderWidth = a.getDimension(R.styleable.ChartAttr_borderWidth,
        attribute.borderWidth);//边框线条宽度

    attribute.borderColor = a.getColor(R.styleable.ChartAttr_borderColor,
        attribute.borderColor);//边框线条颜色

    /**
     * 与 X 轴标尺刻度有关属性
     */
    attribute.axisCount = a.getInteger(R.styleable.ChartAttr_axisCount,
        attribute.axisCount);//行数

    attribute.axisWidth = a.getDimension(R.styleable.ChartAttr_axisWidth,
        attribute.axisWidth);//X轴线条宽度

    attribute.axisColor = a.getColor(R.styleable.ChartAttr_axisColor,
        attribute.axisColor);//X轴线条颜色

    attribute.axisDiffColor = a.getColor(R.styleable.ChartAttr_axisDiffColor,
        attribute.axisDiffColor);//X轴线条diff颜色

    attribute.axisLabelSize = a.getDimension(R.styleable.ChartAttr_axisLabelSize,
        attribute.axisLabelSize);// X 轴标签字符大小

    attribute.axisLabelColor = a.getColor(R.styleable.ChartAttr_axisLabelColor,
        attribute.axisLabelColor);//X 轴标签字符颜色

    attribute.axisLabelMarginTop = a.getDimension(R.styleable.ChartAttr_axisLabelMarginTop,
        attribute.axisLabelMarginTop);//X 轴标签上边距

    attribute.axisLabelMarginBottom = a.getDimension(R.styleable.ChartAttr_axisLabelMarginBottom,
        attribute.axisLabelMarginBottom);// X 轴标签下边距

    /**
     * 与 Y 轴标尺刻度有关属性
     */
    attribute.gridCount = a.getInteger(R.styleable.ChartAttr_gridCount,
        attribute.gridCount);//列数

    attribute.gridWidth = a.getDimension(R.styleable.ChartAttr_gridWidth,
        attribute.gridWidth);//Y轴线条宽度

    attribute.gridColor = a.getColor(R.styleable.ChartAttr_gridColor,
        attribute.gridColor);//Y轴线条颜色

    attribute.gridIsHide = a.getBoolean(R.styleable.ChartAttr_gridIsHide,
        attribute.gridIsHide);//Y轴线条是否显示

    attribute.gridLabelSize = a.getDimension(R.styleable.ChartAttr_gridLabelSize,
        attribute.gridLabelSize);// Y 轴标签字符大小

    attribute.gridLabelColor = a.getColor(R.styleable.ChartAttr_gridLabelColor,
        attribute.gridLabelColor);// Y 轴标签字符颜色

    attribute.gridLabelDiffColor = a.getColor(R.styleable.ChartAttr_gridLabelDiffColor,
        attribute.gridLabelDiffColor);// Y 轴标签字符diff颜色

    attribute.gridLabelLRMargin = a.getDimension(R.styleable.ChartAttr_gridLabelLRMargin,
        attribute.gridLabelLRMargin);//Y 轴标签左右Margin

    attribute.gridLabelTBMargin = a.getDimension(R.styleable.ChartAttr_gridLabelTBMargin,
        attribute.gridLabelTBMargin);//Y 轴标签上下Margin

    int align =
        a.getInteger(R.styleable.ChartAttr_gridLabelLocation, GridLabelLocation.LEFT.ordinal());
    attribute.gridLabelLocation = GridLabelLocation.values()[align];// Y 轴标签显示位置

    /**
     * 与高亮线有关的属性
     */
    attribute.xHighlightAutoWidth = a.getBoolean(R.styleable.ChartAttr_xHighlightAutoWidth,
        attribute.xHighlightAutoWidth);// X高亮线条是否启用自动宽度管理(启用后xHighlightWidth将失效，宽度将实时跟随candleWidth)

    attribute.xHighlightWidth = a.getDimension(R.styleable.ChartAttr_xHighlightWidth,
        attribute.xHighlightWidth);  // X高亮线条宽度

    attribute.xHighlightColor = a.getColor(R.styleable.ChartAttr_xHighlightColor,
        attribute.xHighlightColor);// X高亮线条颜色

    attribute.xHighlightIsHide = a.getBoolean(R.styleable.ChartAttr_xHighlightIsHide,
        attribute.xHighlightIsHide);// X高亮线条是否显示

    attribute.yHighlightAutoWidth = a.getBoolean(R.styleable.ChartAttr_yHighlightAutoWidth,
        attribute.yHighlightAutoWidth);// Y高亮线条是否启用自动宽度管理(启用后yHighlightWidth将失效，宽度将实时跟随candleWidth)

    attribute.yHighlightWidth = a.getDimension(R.styleable.ChartAttr_yHighlightWidth,
        attribute.yHighlightWidth);// Y高亮线条宽度

    attribute.yHighlightColor = a.getColor(R.styleable.ChartAttr_yHighlightColor,
        attribute.yHighlightColor);// Y高亮线条颜色

    attribute.yHighlightIsHide = a.getBoolean(R.styleable.ChartAttr_yHighlightIsHide,
        attribute.yHighlightIsHide);// Y高亮线条是否显示

    /**
     * 与MarkerView 有关的属性
     */
    attribute.markerBorderWidth = a.getDimension(R.styleable.ChartAttr_markerBorderWidth,
        attribute.markerBorderWidth); // MarkerView 边框宽度

    attribute.markerBorderRadius = a.getDimension(R.styleable.ChartAttr_markerBorderRadius,
        attribute.markerBorderRadius);// MarkerView 边框圆角

    attribute.markerBorderTBPadding = a.getDimension(R.styleable.ChartAttr_markerBorderTBPadding,
        attribute.markerBorderTBPadding);// MarkerView 上下padding

    attribute.markerBorderLRPadding = a.getDimension(R.styleable.ChartAttr_markerBorderLRPadding,
        attribute.markerBorderLRPadding);// MarkerView 左右padding

    attribute.markerBorderColor = a.getColor(R.styleable.ChartAttr_markerBorderColor,
        attribute.markerBorderColor);// MarkerView 边框颜色

    attribute.markerTextSize = a.getDimension(R.styleable.ChartAttr_markerTextSize,
        attribute.markerTextSize); // MarkerView 字符大小

    attribute.markerTextColor = a.getColor(R.styleable.ChartAttr_markerTextColor,
        attribute.markerTextColor);// MarkerView 字符颜色

    align = a.getInteger(R.styleable.ChartAttr_xMarkerAlign, AxisMarkerAlign.AUTO.ordinal());
    attribute.xMarkerAlign = AxisMarkerAlign.values()[align];// X 轴 MarkerView 对齐方向

    align = a.getInteger(R.styleable.ChartAttr_yMarkerAlign, GridMarkerAlign.AUTO.ordinal());
    attribute.yMarkerAlign = GridMarkerAlign.values()[align];// Y 轴 MarkerView 对齐方向

    int style = a.getInteger(R.styleable.ChartAttr_markerStyle, Paint.Style.STROKE.ordinal());
    attribute.markerStyle = Paint.Style.values()[style];//  MarkerView 的style（边框/边框和填充）

    /**
     * 与选择器有关的属性
     */
    attribute.selectorPadding = a.getDimension(R.styleable.ChartAttr_selectorPadding,
        attribute.selectorPadding);//信息选择框的padding

    attribute.selectorMarginX = a.getDimension(R.styleable.ChartAttr_selectorMarginX,
        attribute.selectorMarginX);//信息选择框的左右margin

    attribute.selectorMarginY = a.getDimension(R.styleable.ChartAttr_selectorMarginY,
        attribute.selectorMarginY);//信息选择框的上下margin

    attribute.selectorIntervalY = a.getDimension(R.styleable.ChartAttr_selectorIntervalY,
        attribute.selectorIntervalY);//信息选择框的item上下间隔

    attribute.selectorIntervalX = a.getDimension(R.styleable.ChartAttr_selectorIntervalX,
        attribute.selectorIntervalX);//信息选择框的item上下间隔

    attribute.selectorIntervalX = a.getDimension(R.styleable.ChartAttr_selectorIntervalX,
        attribute.selectorIntervalX);//信息选择框的item左右间隔

    attribute.selectorRadius = a.getDimension(R.styleable.ChartAttr_selectorRadius,
        attribute.selectorRadius);//信息选择框的item左右间隔

    attribute.selectorBorderWidth = a.getDimension(R.styleable.ChartAttr_selectorBorderWidth,
        attribute.selectorBorderWidth);//选择器边框线宽度

    attribute.selectorBorderColor = a.getColor(R.styleable.ChartAttr_selectorBorderColor,
        attribute.selectorBorderColor);// 选择器边框线颜色

    attribute.selectorBackgroundColor = a.getColor(R.styleable.ChartAttr_selectorBackgroundColor,
        attribute.selectorBackgroundColor);// 选择器背景颜色

    attribute.selectorLabelColor = a.getColor(R.styleable.ChartAttr_selectorLabelColor,
        attribute.selectorLabelColor);// 选择器label颜色

    attribute.selectorValueColor = a.getColor(R.styleable.ChartAttr_selectorValueColor,
        attribute.selectorValueColor);// 选择器value颜色

    attribute.selectorLabelSize = a.getDimension(R.styleable.ChartAttr_selectorLabelSize,
        attribute.selectorLabelSize);//选择器label文字大小

    attribute.selectorValueSize = a.getDimension(R.styleable.ChartAttr_selectorValueSize,
        attribute.selectorValueSize);//选择器value文字大小

    /**
     * 与指标文字有关的属性
     */
    attribute.indicatorsTextSize = a.getDimension(R.styleable.ChartAttr_indicatorsTextSize,
        attribute.indicatorsTextSize); // 指标文字大小

    attribute.indicatorsTextMarginX = a.getDimension(R.styleable.ChartAttr_indicatorsTextMarginX,
        attribute.indicatorsTextMarginX); // 指标文字左右margin

    attribute.indicatorsTextMarginY = a.getDimension(R.styleable.ChartAttr_indicatorsTextMarginY,
        attribute.indicatorsTextMarginY); // 指标文字上下margin

    attribute.indicatorsTextInterval = a.getDimension(R.styleable.ChartAttr_indicatorsTextInterval,
        attribute.indicatorsTextInterval); // 指标文字的间隔

    /**
     * 涨跌有关的属性
     */
    attribute.increasingColor = a.getColor(R.styleable.ChartAttr_increasingColor,
        attribute.increasingColor);// 上涨颜色

    attribute.decreasingColor = a.getColor(R.styleable.ChartAttr_decreasingColor,
        attribute.decreasingColor);// 下跌颜色

    attribute.increasingDiffColor = a.getColor(R.styleable.ChartAttr_increasingDiffColor,
        attribute.increasingDiffColor);// diff上涨颜色

    attribute.decreasingDiffColor = a.getColor(R.styleable.ChartAttr_decreasingDiffColor,
        attribute.decreasingDiffColor);// diff下跌颜色

    style =
        a.getInteger(R.styleable.ChartAttr_increasingStyle, Paint.Style.FILL.ordinal());
    attribute.increasingStyle = Paint.Style.values()[style];// 上涨蜡烛图填充样式。默认实心

    style =
        a.getInteger(R.styleable.ChartAttr_decreasingStyle, Paint.Style.STROKE.ordinal());
    attribute.decreasingStyle = Paint.Style.values()[style];// 下跌蜡烛图填充样式，默认空心

    /**
     * 缩放有关的属性
     */
    attribute.visibleCount = a.getFloat(R.styleable.ChartAttr_visibleCount,
        attribute.visibleCount);// 竖屏状态下的默认缩放倍数下显示多少个蜡烛图。注：横屏时会自动根据视图宽高变化比例计算，不需要手工设置

    attribute.maxScale = a.getFloat(R.styleable.ChartAttr_maxScale, attribute.maxScale);// 最多放大倍数

    float minScale =
        1f - a.getFloat(R.styleable.ChartAttr_minScale, attribute.minScale) / 10f;// 最多缩小倍数
    attribute.minScale = minScale > 0 ? minScale : 0.1f;

    attribute.currentScale =
        a.getFloat(R.styleable.ChartAttr_currentScale, attribute.currentScale);// 当前X轴缩放倍数

    /**
     * 极值有关属性
     */
    attribute.candleExtremumLabelSize =
        a.getDimension(R.styleable.ChartAttr_candleExtremumLabelSize,
            attribute.candleExtremumLabelSize); // 蜡烛图极值字符大小

    attribute.candleExtremumLableColor = a.getColor(R.styleable.ChartAttr_candleExtremumLableColor,
        attribute.candleExtremumLableColor);// 蜡烛图极值字符颜色

    /**
     * 与股票指标有关的属性
     */
    attribute.centerLineWidth = a.getDimension(R.styleable.ChartAttr_centerLineWidth,
        attribute.centerLineWidth);// 视图中心线宽度

    attribute.centerLineColor = a.getColor(R.styleable.ChartAttr_centerLineColor,
        attribute.centerLineColor);// 视图中心线颜色

    attribute.normLineWidth = a.getDimension(R.styleable.ChartAttr_normLineWidth,
        attribute.normLineWidth);//指标线宽度

    attribute.ma5Color = a.getColor(R.styleable.ChartAttr_ma5Color,
        attribute.ma5Color);// MA5 平均线颜色

    attribute.ma10Color = a.getColor(R.styleable.ChartAttr_ma10Color,
        attribute.ma10Color);// MA10 平均线颜色

    attribute.ma20Color = a.getColor(R.styleable.ChartAttr_ma20Color,
        attribute.ma20Color);// MA20 平均线颜色

    attribute.bollMidLineColor = a.getColor(R.styleable.ChartAttr_bollMidLineColor,
        attribute.bollMidLineColor);// BOLL MID 线条颜色

    attribute.bollUpperLineColor = a.getColor(R.styleable.ChartAttr_bollUpperLineColor,
        attribute.bollUpperLineColor); // BOLL UPPER 线条颜色

    attribute.bollLowerLineColor = a.getColor(R.styleable.ChartAttr_bollLowerLineColor,
        attribute.bollLowerLineColor);// BOLL LOWER 线条颜色

    attribute.kdjKLineColor = a.getColor(R.styleable.ChartAttr_kdjKLineColor,
        attribute.kdjKLineColor);// KDJ K 线条颜色

    attribute.kdjDLineColor = a.getColor(R.styleable.ChartAttr_kdjDLineColor,
        attribute.kdjDLineColor);// KDJ D 线条颜色

    attribute.kdjJLineColor = a.getColor(R.styleable.ChartAttr_kdjJLineColor,
        attribute.kdjJLineColor); // KDJ J 线条颜色

    attribute.deaLineColor = a.getColor(R.styleable.ChartAttr_deaLineColor,
        attribute.deaLineColor);// DEA 线条颜色

    attribute.diffLineColor = a.getColor(R.styleable.ChartAttr_diffLineColor,
        attribute.diffLineColor);// DIFF 线条颜色

    attribute.rsi1LineColor = a.getColor(R.styleable.ChartAttr_rsi1LineColor,
        attribute.rsi1LineColor);// RSI 线条宽度

    attribute.rsi2LineColor = a.getColor(R.styleable.ChartAttr_rsi2LineColor,
        attribute.rsi2LineColor);// RSI 第二条线颜色

    attribute.rsi3LineColor = a.getColor(R.styleable.ChartAttr_rsi3LineColor,
        attribute.rsi3LineColor);// RSI 第三条线颜色

    /**
     *  与loading和error有关的属性
     */
    attribute.loadingTextSize = a.getDimension(R.styleable.ChartAttr_loadingTextSize,
        attribute.loadingTextSize);

    attribute.loadingTextColor = a.getColor(R.styleable.ChartAttr_loadingTextColor,
        attribute.loadingTextColor);

    String loadingText = a.getString(R.styleable.ChartAttr_loadingText);
    if (!TextUtils.isEmpty(loadingText)) {
      attribute.loadingText = loadingText;
    }

    attribute.errorTextSize = a.getDimension(R.styleable.ChartAttr_errorTextSize,
        attribute.errorTextSize);

    attribute.errorTextColor = a.getColor(R.styleable.ChartAttr_errorTextColor,
        attribute.errorTextColor);

    String errorText = a.getString(R.styleable.ChartAttr_errorText);
    if (!TextUtils.isEmpty(errorText)) {
      attribute.errorText = errorText;
    }

    /**
     * 与蜡烛图有关的属性
     */
    if (attribute instanceof CandleAttribute) {
      CandleAttribute candleAttribute = (CandleAttribute) attribute;

      candleAttribute.candleBorderWidth = a.getDimension(R.styleable.ChartAttr_candleBorderWidth,
          candleAttribute.candleBorderWidth);// 蜡烛图矩形边框线宽度

      candleAttribute.candleWidth = a.getDimension(R.styleable.ChartAttr_candleWidth,
          candleAttribute.candleWidth);//蜡烛图初始宽度（缩放都将以此宽度为基准）

      candleAttribute.candleSpace = a.getDimension(R.styleable.ChartAttr_candleSpace,
          candleAttribute.candleSpace);//蜡烛图间隔

      candleAttribute.timeLineWidth = a.getDimension(R.styleable.ChartAttr_timeLineWidth,
          candleAttribute.timeLineWidth);//分时图线条宽度

      candleAttribute.timeLineColor = a.getColor(R.styleable.ChartAttr_timeLineColor,
          candleAttribute.timeLineColor);//分时图线条颜色

      candleAttribute.timeLineShaderColorBegin =
          a.getColor(R.styleable.ChartAttr_timeLineShaderColorBegin,
              candleAttribute.timeLineShaderColorBegin);//分时线颜色渐变起始色值

      candleAttribute.timeLineShaderColorEnd =
          a.getColor(R.styleable.ChartAttr_timeLineShaderColorEnd,
              candleAttribute.timeLineShaderColorEnd);//分时线颜色渐变结束色值
    }

    /**
     * 与深度图有关的属性
     */
    if (attribute instanceof DepthAttribute) {
      DepthAttribute depthAttribute = (DepthAttribute) attribute;

      depthAttribute.polylineWidth = a.getDimension(R.styleable.ChartAttr_depthLineWidth,
          depthAttribute.polylineWidth);// 折线宽度

      depthAttribute.bidLineColor = a.getColor(R.styleable.ChartAttr_depthBidLineColor,
          depthAttribute.bidLineColor);// 买单折线颜色

      depthAttribute.askLineColor = a.getColor(R.styleable.ChartAttr_depthAskLineColor,
          depthAttribute.askLineColor);// 卖单折线颜色

      depthAttribute.bidShaderColor = a.getColor(R.styleable.ChartAttr_depthBidShaderColor,
          depthAttribute.bidShaderColor);// 买单阴影颜色

      depthAttribute.askShaderColor = a.getColor(R.styleable.ChartAttr_depthAskShaderColor,
          depthAttribute.askShaderColor);// 卖单阴影颜色

      depthAttribute.bidHighlightColor = a.getColor(R.styleable.ChartAttr_depthBidHighlightColor,
          depthAttribute.bidHighlightColor);// 买单高亮线颜色

      depthAttribute.askHighlightColor = a.getColor(R.styleable.ChartAttr_depthAskHighlightColor,
          depthAttribute.askHighlightColor);// 卖单高亮线颜色
    }
  }
}
