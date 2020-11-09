
package com.wk.chart.compat.attribute;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.ll.chart.enumeration.IndicatorsLabelLocation;
import com.wk.chart.enumeration.AxisLabelLocation;
import com.wk.chart.enumeration.AxisMarkerAlign;
import com.wk.chart.enumeration.DrawingAlign;
import com.wk.chart.enumeration.ExtremumTagDrawableLocation;
import com.wk.chart.enumeration.GridMarkerAlign;
import com.wk.chart.enumeration.HighLightStyle;

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
    public float viewInterval = 0; // 各个视图模块间的间隔
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
    public boolean onSingleClickSelected = false;//是否可以单击选中

    /**
     * 与 grid 标尺刻度有关属性
     */
    public int gridCount = 4; // grid数量
    public float gridDividingLineWidth = 0; // grid 分割线宽度
    public float gridLabelMarginTop = 6; // grid标签上边距
    public float gridLabelMarginBottom = 6; // grid标签下边距
    public boolean gridLineState = true;//grid 线条是否显示

    /**
     * 与 axis 标尺刻度有关属性
     */
    public int axisCount = 3; // axis数量
    public float axisLabelLRMargin = 16f;//axis标签左右Margin
    public float axisLabelTBMargin = 8f;//axis标签上下Margin
    public AxisLabelLocation axisLabelLocation = AxisLabelLocation.LEFT; // axis标签显示位置
    public boolean axisLineState = true;//axis 线条是否显示

    /**
     * 与高亮线有关的属性
     */
    public boolean xHighlightIsHide = false; // X高亮线条是否显示
    // X高亮线条是否启用自动宽度管理(启用后xHighlightWidth将失效，宽度将实时跟随candleWidth)
    public boolean xHighlightAutoWidth = false;
    public int xHighlightColor = 0x33ffffff; // X高亮线条颜色 0x33ffffff

    public boolean yHighlightIsHide = false; // Y高亮线条是否显示
    // Y高亮线条是否启用自动宽度管理(启用后yHighlightWidth将失效，宽度将实时跟随candleWidth)
    public boolean yHighlightAutoWidth = false;
    public int yHighlightColor = 0x33ffffff; // Y高亮线条颜色 0x33ffffff
    public HighLightStyle highLightStyle = HighLightStyle.SOLID; // 高亮线条样式(实线/虚线)

    /**
     * 与MarkerView 有关的属性
     */
    public float markerBorderWidth = 3f; // MarkerView 边框宽度
    public float markerBorderRadius = 0; // MarkerView 边框圆角
    public float markerBorderTBPadding = 5f; // MarkerView 上下padding
    public float markerBorderLRPadding = 10f; // MarkerView 左右padding
    public int markerBorderColor = 0xff4d6370; // MarkerView 边框颜色
    public float markerTextSize = 26; // MarkerView 字符大小
    public int markerTextColor = 0xffffffff; // MarkerView 字符颜色
    public Paint.Style markerStyle = Paint.Style.STROKE; //  MarkerView 的style（边框/边框和填充）
    public AxisMarkerAlign axisMarkerAlign = AxisMarkerAlign.AUTO; // X 轴 MarkerView 对齐方向
    public GridMarkerAlign gridMarkerAlign = GridMarkerAlign.BOTTOM_INSIDE; // Y 轴 MarkerView 对齐方向

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
    public int selectorBorderColor = 0x55bdd9e6;//选择器边框线颜色
    public int selectorBackgroundColor = 0xE625383F;//选择器背景颜色
    public int selectorLabelColor = 0xffbdd9e6;//选择器label颜色
    public int selectorValueColor = 0xffbdd9e6;//选择器value颜色
    public float selectorLabelSize = 26;//选择器label文字大小
    public float selectorValueSize = 26;//选择器value文字大小

    /**
     * 与指标文字有关的属性
     */
    public float indicatorsTextSize = 26;//指标文字大小
    public float indicatorsTextMarginX = 0;//指标文字左右margin
    public float indicatorsTextMarginY = 8;//指标文字上下margin
    public float indicatorsTextInterval = 16;//指标文字的间隔
    public boolean defaultShowLastItem = false;//默认显示最后一条数据
    public IndicatorsLabelLocation indicatorsLabelLocation = IndicatorsLabelLocation.LEFT_TOP;
    //指标文字的位置

    /**
     * 与游标指示器有关的属性
     */
    public float cursorBorderWidth = 3;//游标文字容器边框宽度
    public int cursorBackgroundColor = 0xFF202a33;//游标文字容器背景颜色
    public float cursorRadius = 5;//游标文字容器圆角
    public int cursorLineColor = 0xff00efff;//游标线颜色
    public int cursorTextColor = 0xff00efff;//游标值颜色
    public int cursorBorderColor = 0xff00efff;//游标值容器边框颜色

    /**
     * 涨跌有关的属性
     */
    public int increasingColor = 0xffB5FC00; // 上涨颜色（亮色）
    public int decreasingColor = 0xffFE0D5E; // 下跌颜色（亮色）
    public float darkColorAlpha = 1f; // （暗色）透明度（基于涨跌色配合透明度来实现暗色）
    public float shaderBeginColorAlpha = 0.35f;// 阴影开始颜色的透明度
    public float shaderEndColorAlpha = 0.05f;// 阴影结束颜色的透明度
    public Paint.Style increasingStyle = Paint.Style.FILL; // 上涨蜡烛图填充样式。默认实心
    public Paint.Style decreasingStyle = Paint.Style.STROKE; // 下跌蜡烛图填充样式，默认空心

    /**
     * 缩放有关的属性
     */
    public boolean canScroll = true;// 能否滚动
    public float pointBorderWidth = 3f; // 数据点矩形边框线宽度
    public float pointSpace = 8f;//数据点间隔
    public float pointWidth = 28f;//数据点初始宽度（缩放都将以此宽度为基准）
    public float visibleCount = 20; // 竖屏状态下的默认缩放倍数下显示多少个蜡烛图。注：横屏时会自动根据视图宽高变化比例计算，不需要手工设置
    public float maxScale = 10;// 最多放大倍数
    public float minScale = 7;// 最多缩小倍数(缩小后的宽度不会小于 蜡烛图矩形边框线宽度（candleBorderWidth）)
    public float currentScale = 1;// 当前X轴缩放倍数

    /**
     * 极值有关属性
     */
    public float candleExtremumLabelSize = 26; // 极值字符大小
    public int candleExtremumLableColor = 0xffffffff; // 极值字符颜色
    public boolean extremumLineState = true;// 极值横线是否显示
    public Drawable extremumTagDrawable = null;//极值标签Drawable
    public float extremumTagDrawableWidth = 0;//极值标签Drawable宽度
    public float extremumTagDrawableHeight = 0;//极值标签Drawable高度
    public float extremumTagDrawableMarginX = 10;//极值标签Drawable左右margin
    public int extremumTagDrawableLocation = ExtremumTagDrawableLocation.MAX;//极值标签的Drawable显示位置

    /**
     * 与指标有关的属性
     */
    public int centerLineColor = 0x1Affffff; // 视图中心线颜色
    public int indexTagColor = 0xffFF9F00; // 指标Tag颜色
    public int line1Color = 0xff9660c4; // 线条1颜色
    public int line2Color = 0xff84aad5; // 线条2颜色
    public int line3Color = 0xff55b263; // 线条3颜色
    public int line4Color = 0xff7F9976; // 线条4颜色
    public int line5Color = 0xff34a9ff; // 线条5颜色

    /**
     * 与水印有关的属性
     */
    public float waterMarkingWidth = 0;//水印宽度
    public float waterMarkingHeight = 0;//水印高度
    public float waterMarkingMarginX = 30;//水印左右margin
    public float waterMarkingMarginY = 55;//水印上下margin
    public Drawable waterMarkingDrawable = null;//水印Drawable
    public int waterMarkingAlign = DrawingAlign.LEFT | DrawingAlign.BOTTOM;//水印对其齐方向

    /**
     * 与呼吸灯有关的属性
     */
    public float breathingLampRadius = 5;//呼吸灯圆点半径
    public int breathingLampColor = 0xFFFFFFFF;//呼吸灯颜色
    public int breathingLampAutoTwinkleInterval = 1100;//呼吸灯自动闪烁时间（0为不自动闪烁）

    /**
     * 与标记点有关的属性
     */
    public float markerPointTextMarginY = 8;//标记点文字上下边距
    public float markerPointTextMarginX = 12;//标记点文字左右边距
    public float markerPointMinMargin = 2;//标记点最小边距
    public float markerPointLineWidth = 2;//标记点连接线宽度
    public float markerPointLineDefaultLength = 20;//标记点连接线默认长度
    public float markerPointJointRadius = 8;//标记点接点半径（小圆点和小三角）
    public float markerPointJointMargin = 6;//标记点接点边距（小圆点距离K线柱的距离）
    public float markerPointTextSize = 26;//标记点文字大小
    public int markerPointTextColor = 0xFFFFFFFF;//标记点文字颜色
    public int markerPointColorB = 0xFFFF8100;//B标记点颜色
    public int markerPointColorS = 0xFF00ABFF;//S标记点颜色
    public int markerPointColorT = 0xFF27BCC4;//T标记点颜色

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