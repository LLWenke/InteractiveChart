
package com.wk.chart.compat.attribute;

import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.TextUtils;

import com.wk.chart.R;
import com.wk.chart.enumeration.ScaleLineStyle;

/**
 * <p>AttributeRead</p>
 */

public class AttributeRead {

    /**
     * 初始化 BaseAttribute
     */
    public void initAttribute(TypedArray a, BaseAttribute attribute) {
        /*
         * 各个视图模块的配置信息有关属性
         */
        attribute.mainViewHeight = a.getDimension(R.styleable.ChartView_mainViewHeight,
                attribute.mainViewHeight);// 主图模块高度

        attribute.auxiliaryViewHeight = a.getDimension(R.styleable.ChartView_auxiliaryViewHeight,
                attribute.auxiliaryViewHeight);// 副图模块高度

        attribute.indexViewHeight = a.getDimension(R.styleable.ChartView_indexViewHeight,
                attribute.indexViewHeight);// 指标模块高度

        attribute.viewInterval = a.getDimension(R.styleable.ChartView_viewInterval,
                attribute.viewInterval);// 各个视图模块间的间隔

        attribute.leftScrollOffset = a.getDimension(R.styleable.ChartView_leftScrollOffset,
                attribute.leftScrollOffset);//X 轴方向的最小滚动值固定偏移量（左边）

        attribute.rightScrollOffset = a.getDimension(R.styleable.ChartView_rightScrollOffset,
                attribute.rightScrollOffset);//X 轴方向的最大滚动值固定偏移量（右边）

        /*
         * 共用的有关属性
         */
        attribute.lineWidth = a.getDimension(R.styleable.ChartView_lineWidth,
                attribute.lineWidth);//线条宽度

        attribute.lineColor = a.getColor(R.styleable.ChartView_lineColor,
                attribute.lineColor);//线条颜色

        attribute.labelSize = a.getDimension(R.styleable.ChartView_labelSize,
                attribute.labelSize);//标签字符大小

        attribute.labelColor = a.getColor(R.styleable.ChartView_labelColor,
                attribute.labelColor);//标签字符颜色

        /*
         * 边框线有关属性
         */
        attribute.borderWidth = a.getDimension(R.styleable.ChartView_borderWidth,
                attribute.borderWidth);//边框线条宽度

        attribute.borderColor = a.getColor(R.styleable.ChartView_borderColor,
                attribute.borderColor);//边框线条颜色
        /*
         * 与 grid 标尺刻度有关属性
         */
        attribute.gridCount = a.getInteger(R.styleable.ChartView_gridCount,
                attribute.gridCount);//grid 数量

        attribute.gridLabelMarginVertical = a.getDimension(R.styleable.ChartView_gridLabelMarginVertical,
                attribute.gridLabelMarginVertical);//grid 标签下边距

        attribute.gridScaleShortLineLength = a.getDimension(R.styleable.ChartView_gridScaleShortLineLength,
                attribute.gridScaleShortLineLength);//grid 刻度短线长度

        int style = a.getInteger(R.styleable.ChartView_gridScaleLineStyle, attribute.gridScaleLineStyle.ordinal());
        attribute.gridScaleLineStyle = ScaleLineStyle.values()[style];//grid 线条样式

        /*
         * 与 axis 标尺刻度有关属性
         */
        attribute.axisLabelMarginHorizontal = a.getDimension(R.styleable.ChartView_axisLabelMarginHorizontal,
                attribute.axisLabelMarginHorizontal);//axis 标签水平Margin

        attribute.axisLabelMarginVertical = a.getDimension(R.styleable.ChartView_axisLabelMarginVertical,
                attribute.axisLabelMarginVertical);//axis 标签垂直Margin

        attribute.axisScaleShortLineLength = a.getDimension(R.styleable.ChartView_axisScaleShortLineLength,
                attribute.axisScaleShortLineLength);//axis 刻度短线长度

        attribute.axisShowFirst = a.getBoolean(R.styleable.ChartView_axisShowFirst,
                attribute.axisShowFirst);//axis 是否显示第一条

        attribute.axisShowLast = a.getBoolean(R.styleable.ChartView_axisShowLast,
                attribute.axisShowLast);//axis 是否显示最后一条

        style = a.getInteger(R.styleable.ChartView_axisScaleLineStyle, attribute.axisScaleLineStyle.ordinal());
        attribute.axisScaleLineStyle = ScaleLineStyle.values()[style];//axis 线条样式

        attribute.axisLabelPosition = a.getInt(R.styleable.ChartView_axisLabelPosition, attribute.axisLabelPosition); //axis 标签位置

        /*
         * 与高亮线有关的属性
         */
        attribute.xHighlightAutoWidth = a.getBoolean(R.styleable.ChartView_xHighlightAutoWidth,
                attribute.xHighlightAutoWidth);// X高亮线条是否启用自动宽度管理(启用后xHighlightWidth将失效，宽度将实时跟随candleWidth)

        attribute.xHighlightColor = a.getColor(R.styleable.ChartView_xHighlightColor,
                attribute.xHighlightColor);// X高亮线条颜色

        attribute.xHighlightIsHide = a.getBoolean(R.styleable.ChartView_xHighlightIsHide,
                attribute.xHighlightIsHide);// X高亮线条是否显示

        attribute.yHighlightAutoWidth = a.getBoolean(R.styleable.ChartView_yHighlightAutoWidth,
                attribute.yHighlightAutoWidth);// Y高亮线条是否启用自动宽度管理(启用后yHighlightWidth将失效，宽度将实时跟随candleWidth)

        attribute.yHighlightAutoDivision = a.getBoolean(R.styleable.ChartView_yHighlightAutoDivision,
                attribute.yHighlightAutoDivision);// Y高亮线条是否启用自动分割

        attribute.yHighlightColor = a.getColor(R.styleable.ChartView_yHighlightColor,
                attribute.yHighlightColor);// Y高亮线条颜色

        attribute.yHighlightIsHide = a.getBoolean(R.styleable.ChartView_yHighlightIsHide,
                attribute.yHighlightIsHide);// Y高亮线条是否显示

        /*
         * 与MarkerView 有关的属性
         */
        attribute.markerRadius = a.getDimension(R.styleable.ChartView_markerRadius,
                attribute.markerRadius);// MarkerView 边框圆角

        attribute.markerPaddingVertical = a.getDimension(R.styleable.ChartView_markerPaddingVertical,
                attribute.markerPaddingVertical);// MarkerView 垂直padding

        attribute.markerPaddingHorizontal = a.getDimension(R.styleable.ChartView_markerPaddingHorizontal,
                attribute.markerPaddingHorizontal);// MarkerView 水平padding

        attribute.markerBorderWidth = a.getDimension(R.styleable.ChartView_markerBorderWidth,
                attribute.markerBorderWidth); // MarkerView 边框宽度

        attribute.markerBorderColor = a.getColor(R.styleable.ChartView_markerBorderColor,
                attribute.markerBorderColor);// MarkerView 边框颜色

        attribute.markerTextSize = a.getDimension(R.styleable.ChartView_markerTextSize,
                attribute.markerTextSize); // MarkerView 字符大小

        attribute.markerTextColor = a.getColor(R.styleable.ChartView_markerTextColor,
                attribute.markerTextColor);// MarkerView 字符颜色

        attribute.gridMarkerPosition = a.getInt(R.styleable.ChartView_gridMarkerPosition,
                attribute.gridMarkerPosition);// grid 轴 MarkerView 位置

        attribute.axisMarkerPosition = a.getInt(R.styleable.ChartView_axisMarkerPosition,
                attribute.axisMarkerPosition);// axis 轴 MarkerView 位置

        style = a.getInteger(R.styleable.ChartView_markerStyle, attribute.markerStyle.ordinal());
        attribute.markerStyle = Paint.Style.values()[style];//  MarkerView 的style（边框/边框和填充）

        /*
         * 与选择器有关的属性
         */
        attribute.selectorPadding = a.getDimension(R.styleable.ChartView_selectorPadding,
                attribute.selectorPadding);//信息选择框的padding

        attribute.selectorMarginHorizontal = a.getDimension(R.styleable.ChartView_selectorMarginHorizontal,
                attribute.selectorMarginHorizontal);//信息选择框的水平margin

        attribute.selectorMarginVertical = a.getDimension(R.styleable.ChartView_selectorMarginVertical,
                attribute.selectorMarginVertical);//信息选择框的垂直margin

        attribute.selectorIntervalVertical = a.getDimension(R.styleable.ChartView_selectorIntervalVertical,
                attribute.selectorIntervalVertical);//信息选择框的item垂直间隔

        attribute.selectorIntervalHorizontal = a.getDimension(R.styleable.ChartView_selectorIntervalHorizontal,
                attribute.selectorIntervalHorizontal);//信息选择框的item垂直间隔

        attribute.selectorIntervalHorizontal = a.getDimension(R.styleable.ChartView_selectorIntervalHorizontal,
                attribute.selectorIntervalHorizontal);//信息选择框的item水平间隔

        attribute.selectorRadius = a.getDimension(R.styleable.ChartView_selectorRadius,
                attribute.selectorRadius);//信息选择框圆角

        attribute.selectorBorderWidth = a.getDimension(R.styleable.ChartView_selectorBorderWidth,
                attribute.selectorBorderWidth);//选择器边框线宽度

        attribute.selectorBorderColor = a.getColor(R.styleable.ChartView_selectorBorderColor,
                attribute.selectorBorderColor);// 选择器边框线颜色

        attribute.selectorBackgroundColor = a.getColor(R.styleable.ChartView_selectorBackgroundColor,
                attribute.selectorBackgroundColor);// 选择器背景颜色

        attribute.selectorLabelColor = a.getColor(R.styleable.ChartView_selectorLabelColor,
                attribute.selectorLabelColor);// 选择器label颜色

        attribute.selectorValueColor = a.getColor(R.styleable.ChartView_selectorValueColor,
                attribute.selectorValueColor);// 选择器value颜色

        attribute.selectorLabelSize = a.getDimension(R.styleable.ChartView_selectorLabelSize,
                attribute.selectorLabelSize);//选择器label文字大小

        attribute.selectorValueSize = a.getDimension(R.styleable.ChartView_selectorValueSize,
                attribute.selectorValueSize);//选择器value文字大小

        /*
         * 与指标文字有关的属性
         */
        attribute.indexTextSize = a.getDimension(R.styleable.ChartView_indexTextSize,
                attribute.indexTextSize); // 指标文字大小

        attribute.indexTextMarginHorizontal = a.getDimension(R.styleable.ChartView_indexTextMarginHorizontal,
                attribute.indexTextMarginHorizontal); // 指标文字水平margin

        attribute.indexTextMarginVertical = a.getDimension(R.styleable.ChartView_indexTextMarginVertical,
                attribute.indexTextMarginVertical); // 指标文字垂直margin

        attribute.indexTextInterval = a.getDimension(R.styleable.ChartView_indexTextInterval,
                attribute.indexTextInterval); // 指标文字的间隔

        attribute.indexDefaultShowLastItemInfo = a.getBoolean(R.styleable.ChartView_defaultShowLastItem,
                attribute.indexDefaultShowLastItemInfo);// 指标默认显示最后一条的数据

        attribute.indexLabelPosition = a.getInt(R.styleable.ChartView_indexLabelPosition,
                attribute.indexLabelPosition);// 指标文字的位置

        /*
         * 与游标指示器有关的属性
         */
        attribute.cursorBackgroundColor = a.getColor(R.styleable.ChartView_cursorBackgroundColor,
                attribute.cursorBackgroundColor);// 游标文字容器背景颜色

        attribute.foldedCursorLineColor = a.getColor(R.styleable.ChartView_foldedCursorLineColor,
                attribute.foldedCursorLineColor);// （折叠时）游标线颜色

        attribute.foldedCursorTextColor = a.getColor(R.styleable.ChartView_foldedCursorTextColor,
                attribute.foldedCursorTextColor);// （折叠时）游标值颜色

        attribute.spreadCursorLineColor = a.getColor(R.styleable.ChartView_spreadCursorLineColor,
                attribute.spreadCursorLineColor);// （展开时）游标线颜色

        attribute.spreadCursorTextColor = a.getColor(R.styleable.ChartView_spreadCursorTextColor,
                attribute.spreadCursorTextColor);// （展开时）游标值颜色

        attribute.spreadCursorBorderColor = a.getColor(R.styleable.ChartView_spreadCursorBorderColor,
                attribute.spreadCursorBorderColor);// （展开时）游标值容器边框颜色

        attribute.spreadCursorBorderWidth = a.getDimension(R.styleable.ChartView_spreadCursorBorderWidth,
                attribute.spreadCursorBorderWidth); //（展开时） 游标文字容器边框宽度

        attribute.spreadCursorRadius = a.getDimension(R.styleable.ChartView_spreadCursorRadius,
                attribute.spreadCursorRadius); //（展开时） 游标文字容器圆角

        attribute.spreadCursorTextMarginHorizontal = a.getDimension(R.styleable.ChartView_spreadCursorTextMarginHorizontal,
                attribute.spreadCursorTextMarginHorizontal); //（展开时） 游标文字水平Margin

        attribute.spreadCursorTextMarginVertical = a.getDimension(R.styleable.ChartView_spreadCursorTextMarginVertical,
                attribute.spreadCursorTextMarginVertical); //（展开时） 游标文字垂直Margin

        attribute.spreadTriangleWidth = a.getDimension(R.styleable.ChartView_spreadTriangleWidth,
                attribute.spreadTriangleWidth); //（展开时） 游标三角宽度

        attribute.spreadTriangleHeight = a.getDimension(R.styleable.ChartView_spreadTriangleHeight,
                attribute.spreadTriangleHeight); //（展开时） 游标三角高度

        /*
         * 极值Label有关属性
         */
        attribute.extremumLabelMarginHorizontal = a.getDimension(R.styleable.ChartView_extremumLabelMarginHorizontal,
                attribute.extremumLabelMarginHorizontal); // 极值Label的水平边距
        attribute.extremumLabelMarginVertical = a.getDimension(R.styleable.ChartView_extremumLabelMarginVertical,
                attribute.extremumLabelMarginVertical); // 极值Label的垂直边距
        attribute.extremumLabelPosition = a.getInt(R.styleable.ChartView_extremumLabelPosition,
                attribute.extremumLabelPosition); // 极值Label的位置

        /*
         * 极值Tag有关属性
         */
        attribute.candleExtremumLabelSize = a.getDimension(R.styleable.ChartView_candleExtremumLabelSize,
                attribute.candleExtremumLabelSize); // 极值字符大小

        attribute.candleExtremumLableColor = a.getColor(R.styleable.ChartView_candleExtremumLableColor,
                attribute.candleExtremumLableColor);// 极值字符颜色

        attribute.extremumTagDrawable = a.getDrawable(R.styleable.ChartView_extremumTagDrawable); //最极值标签Drawable

        attribute.extremumTagDrawableWidth = a.getDimension(R.styleable.ChartView_extremumTagDrawableWidth,
                attribute.extremumTagDrawableWidth);//极值标签Drawable宽度

        attribute.extremumTagDrawableHeight = a.getDimension(R.styleable.ChartView_extremumTagDrawableHeight,
                attribute.extremumTagDrawableHeight);//极值标签Drawable高度

        attribute.extremumTagDrawableMarginHorizontal = a.getDimension(R.styleable.ChartView_extremumTagDrawableMarginVertical,
                attribute.extremumTagDrawableMarginHorizontal);//极值标签Drawable水平margin

        attribute.extremumTagDrawableVisible = a.getInt(R.styleable.ChartView_extremumTagDrawableVisible,
                attribute.extremumTagDrawableVisible);//极值标签的Drawable显示模式

        /*
         * 涨跌有关的属性
         */
        attribute.increasingColor = a.getColor(R.styleable.ChartView_increasingColor,
                attribute.increasingColor);// 上涨颜色

        attribute.decreasingColor = a.getColor(R.styleable.ChartView_decreasingColor,
                attribute.decreasingColor);// 下跌颜色

        attribute.shaderBeginColorAlpha = getAlpha(a.getFloat(R.styleable.ChartView_shaderBeginColorAlpha,
                attribute.shaderBeginColorAlpha));//阴影开始颜色的透明度

        attribute.shaderEndColorAlpha = getAlpha(a.getFloat(R.styleable.ChartView_shaderEndColorAlpha,
                attribute.shaderEndColorAlpha));//阴影结束颜色的透明度

        attribute.darkColorAlpha = getAlpha(a.getFloat(R.styleable.ChartView_darkColorAlpha,
                attribute.darkColorAlpha));//（暗色）透明度（基于涨跌色配合透明度来实现暗色）

        style = a.getInteger(R.styleable.ChartView_increasingStyle, attribute.increasingStyle.ordinal());
        attribute.increasingStyle = Paint.Style.values()[style];// 上涨蜡烛图填充样式。默认实心

        style = a.getInteger(R.styleable.ChartView_decreasingStyle, attribute.decreasingStyle.ordinal());
        attribute.decreasingStyle = Paint.Style.values()[style];// 下跌蜡烛图填充样式，默认空心

        /*
         * 缩放有关的属性
         */
        attribute.pointBorderWidth = a.getDimension(R.styleable.ChartView_pointBorderWidth,
                attribute.pointBorderWidth);//数据点矩形边框线宽度

        attribute.pointWidth = a.getDimension(R.styleable.ChartView_pointWidth,
                attribute.pointWidth);//数据点初始宽度（缩放都将以此宽度为基准）

        attribute.pointSpace = a.getDimension(R.styleable.ChartView_pointSpace,
                attribute.pointSpace);//数据点间隔

        attribute.canScroll = a.getBoolean(R.styleable.ChartView_canScroll,
                attribute.canScroll);// 能否滚动

        attribute.maxScale = a.getFloat(R.styleable.ChartView_maxScale, attribute.maxScale);// 最多放大倍数

        float minScale = 1f - a.getFloat(R.styleable.ChartView_minScale, attribute.minScale) / 10f;// 最多缩小倍数
        attribute.minScale = minScale > 0 ? minScale : 0.1f;

        attribute.currentScale =
                a.getFloat(R.styleable.ChartView_currentScale, attribute.currentScale);// 当前缩放倍数

        /*
         * 与股票指标有关的属性
         */
        attribute.centerLineColor = a.getColor(R.styleable.ChartView_centerLineColor,
                attribute.centerLineColor);// 视图中心线颜色

        attribute.indexTagColor = a.getColor(R.styleable.ChartView_indexTagColor,
                attribute.indexTagColor);// 指标Tag颜色

        /*
         * 与水印有关的属性
         */
        attribute.waterMarkingWidth = a.getDimension(R.styleable.ChartView_waterMarkingWidth,
                attribute.waterMarkingWidth);//水印宽度

        attribute.waterMarkingHeight = a.getDimension(R.styleable.ChartView_waterMarkingHeight,
                attribute.waterMarkingHeight);//水印高度

        attribute.waterMarkingMarginHorizontal = a.getDimension(R.styleable.ChartView_waterMarkingMarginHorizontal,
                attribute.waterMarkingMarginHorizontal);//水印水平margin

        attribute.waterMarkingMarginVertical = a.getDimension(R.styleable.ChartView_waterMarkingMarginVertical,
                attribute.waterMarkingMarginVertical);//水印垂直margin

        //水印Drawable
        attribute.waterMarkingDrawable = a.getDrawable(R.styleable.ChartView_waterMarkingDrawable);

        attribute.waterMarkingPosition = a.getInt(R.styleable.ChartView_waterMarkingPosition,
                attribute.waterMarkingPosition);//水印位置

        /*
         * 与呼吸灯有关的属性
         */
        attribute.breathingLampRadius = a.getDimension(R.styleable.ChartView_breathingLampRadius,
                attribute.breathingLampRadius);//呼吸灯圆点半径

        attribute.breathingLampColor = a.getColor(R.styleable.ChartView_breathingLampColor,
                attribute.breathingLampColor);//呼吸灯颜色

        attribute.breathingLampAutoTwinkleInterval = a.getInt(R.styleable.ChartView_breathingLampAutoTwinkleInterval,
                attribute.breathingLampAutoTwinkleInterval);//呼吸灯自动闪烁时间（0为不自动闪烁）


        /*
         * 与标记点有关的属性
         */
        attribute.markerPointMinMargin = a.getDimension(R.styleable.ChartView_markerPointMinMargin,
                attribute.markerPointMinMargin);//标记点最小边距

        attribute.markerPointTextMarginVertical = a.getDimension(R.styleable.ChartView_markerPointTextMarginVertical,
                attribute.markerPointTextMarginVertical);//标记点文字垂直边距

        attribute.markerPointTextMarginHorizontal = a.getDimension(R.styleable.ChartView_markerPointTextMarginHorizontal,
                attribute.markerPointTextMarginHorizontal);//标记点文字水平边距

        attribute.markerPointLineWidth = a.getDimension(R.styleable.ChartView_markerPointLineWidth,
                attribute.markerPointLineWidth);//标记点连接线宽度

        attribute.markerPointLineDefaultLength = a.getDimension(R.styleable.ChartView_markerPointLineDefaultLength,
                attribute.markerPointLineDefaultLength);//标记点连接线默认长度

        attribute.markerPointJointRadius = a.getDimension(R.styleable.ChartView_markerPointJointRadius,
                attribute.markerPointJointRadius);//标记点接点半径（小圆点和小三角）

        attribute.markerPointJointMargin = a.getDimension(R.styleable.ChartView_markerPointJointMargin,
                attribute.markerPointJointMargin);//标记点接点边距（小圆点距离K线柱的距离）

        attribute.markerPointTextSize = a.getDimension(R.styleable.ChartView_markerPointTextSize,
                attribute.markerPointTextSize);//标记点文字大小

        attribute.markerPointTextColor = a.getColor(R.styleable.ChartView_markerPointTextColor,
                attribute.markerPointTextColor);//标记点文字颜色

        attribute.markerPointColorB = a.getColor(R.styleable.ChartView_markerPointColorB,
                attribute.markerPointColorB);//B标记点颜色

        attribute.markerPointColorS = a.getColor(R.styleable.ChartView_markerPointColorS,
                attribute.markerPointColorS);//S标记点颜色

        attribute.markerPointColorT = a.getColor(R.styleable.ChartView_markerPointColorT,
                attribute.markerPointColorT);//T标记点颜色


        /*
         *  与loading和error有关的属性
         */
        attribute.loadingTextSize = a.getDimension(R.styleable.ChartView_loadingTextSize,
                attribute.loadingTextSize);

        attribute.loadingTextColor = a.getColor(R.styleable.ChartView_loadingTextColor,
                attribute.loadingTextColor);

        String loadingText = a.getString(R.styleable.ChartView_loadingText);
        if (!TextUtils.isEmpty(loadingText)) {
            attribute.loadingText = loadingText;
        }

        attribute.errorTextSize = a.getDimension(R.styleable.ChartView_errorTextSize,
                attribute.errorTextSize);

        attribute.errorTextColor = a.getColor(R.styleable.ChartView_errorTextColor,
                attribute.errorTextColor);

        String errorText = a.getString(R.styleable.ChartView_errorText);
        if (!TextUtils.isEmpty(errorText)) {
            attribute.errorText = errorText;
        }

        /*
         * 与蜡烛图有关的属性
         */
        if (attribute instanceof CandleAttribute) {
            CandleAttribute candleAttribute = (CandleAttribute) attribute;

            candleAttribute.timeLineWidth = a.getDimension(R.styleable.ChartView_timeLineWidth,
                    candleAttribute.timeLineWidth);//分时图线条宽度

            candleAttribute.timeLineColor = a.getColor(R.styleable.ChartView_timeLineColor,
                    candleAttribute.timeLineColor);//分时图线条颜色
        }

        /*
         * 与深度图有关的属性
         */
        if (attribute instanceof DepthAttribute) {
            DepthAttribute depthAttribute = (DepthAttribute) attribute;

            depthAttribute.polylineWidth = a.getDimension(R.styleable.ChartView_depthLineWidth,
                    depthAttribute.polylineWidth);// 折线宽度

            depthAttribute.circleSize = a.getDimension(R.styleable.ChartView_circleSize,
                    depthAttribute.circleSize);// 圆点大小

            depthAttribute.depthGridStyle = a.getInteger(R.styleable.ChartView_depthGridStyle,
                    depthAttribute.depthGridStyle);// 深度图grid样式
        }
    }

    /**
     * 获取正确的透明度
     *
     * @param alpha 透明度
     * @return 正确的透明度
     */
    private float getAlpha(float alpha) {
        alpha = Math.max(0.0f, alpha);
        alpha = Math.min(1.0f, alpha);
        return alpha;
    }
}
