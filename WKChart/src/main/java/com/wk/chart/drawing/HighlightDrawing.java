
package com.wk.chart.drawing;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.HighLightStyle;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.marker.AbsMarker;
import com.wk.chart.marker.AxisTextMarker;
import com.wk.chart.module.FloatModule;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;
import com.wk.chart.render.CandleRender;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>HighlightDrawing</p>
 */

public class HighlightDrawing extends AbsDrawing<CandleRender, FloatModule> {
    private static final String TAG = "HighlightDrawing";
    private CandleAttribute attribute;//配置文件

    private final Paint axisHighlightPaint = new Paint(); // axis高亮线条画笔
    private final Paint gridHighlightPaint = new Paint(); // grid高亮线条画笔
    private final Path highlightPath = new Path();

    private final float[] markerViewInfo = new float[4];//markerView的left,top,right,bottom信息
    private final float[] highlightPoint = new float[2];//高亮线条x，y
    private final String[] markerText = new String[2];//marker中显示的值
    private final List<AbsMarker<AbsRender<?, ?>>> markerViewList = new ArrayList<>();//标签集合

    public void addMarkerView(AbsMarker<AbsRender<?, ?>> markerView) {
        markerViewList.add(markerView);
    }

    public List<AbsMarker<AbsRender<?, ?>>> getMarkerViewList() {
        return markerViewList;
    }

    @Override
    public void onInit(CandleRender render, FloatModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        axisHighlightPaint.setStyle(Paint.Style.STROKE);
        axisHighlightPaint.setColor(attribute.axisHighlightColor);

        gridHighlightPaint.setStyle(Paint.Style.STROKE);
        gridHighlightPaint.setColor(attribute.gridHighlightColor);

        if (attribute.highLightStyle == HighLightStyle.DOTTED) {
            DashPathEffect dashPathEffect = new DashPathEffect(new float[]{10f, 5f}, 0);
            axisHighlightPaint.setPathEffect(dashPathEffect);
            gridHighlightPaint.setPathEffect(dashPathEffect);
        }

        for (AbsMarker<AbsRender<?, ?>> markerView : markerViewList) {
            markerView.onInit(render);
        }
    }

    @Override
    public float[] onInitMargin() {
        for (AbsMarker<AbsRender<?, ?>> markerView : markerViewList) {
            float[] markerMargin = markerView.onInitMargin();
            margin[0] = Math.max(margin[0], markerMargin[0]);
            margin[1] = Math.max(margin[1], markerMargin[1]);
            margin[2] = Math.max(margin[2], markerMargin[2]);
            margin[3] = Math.max(margin[3], markerMargin[3]);
        }
        return margin;
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void drawOver(Canvas canvas) {
        if (!render.isHighlight()) {
            return;
        }
        float left, top, right, bottom;
        boolean isReverseMarker;
        CandleEntry entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
        //获取当前焦点区域内的chartModule
        AbsModule<? extends AbsEntry> chartModule = attribute.axisHighlightLabelAutoSelect ?
                render.getFocusModuleCache() : render.getMainModule();
        if (null == chartModule) {
            return;
        }
        //获取高亮点的值
        switch (chartModule.getModuleType()) {
            case ModuleType.VOLUME://交易量 指标
                highlightPoint[1] = entry.getVolume().value;
                render.mapPoints(chartModule.getMatrix(), highlightPoint);
                markerText[0] = render.getAdapter().quantizationConversion(entry.getVolume(), true);
                isReverseMarker = true;
                break;
            case ModuleType.CANDLE://k线图 指标
            case ModuleType.TIME://分时图 指标
                highlightPoint[1] = entry.getClose().value;
                render.mapPoints(chartModule.getMatrix(), highlightPoint);
                markerText[0] = render.getAdapter().rateConversion(entry.getClose(), false, false);
                isReverseMarker = false;
                break;
            case ModuleType.MUTATION:  // 指标
                highlightPoint[1] = render.getHighlightPoint()[1];
                render.invertMapPoints(chartModule.getMatrix(), highlightPoint);
                markerText[0] = render.getAdapter().rateConversion(highlightPoint[1], render.getAdapter().getScale().getQuoteScale(), false, false);
                highlightPoint[1] = render.getHighlightPoint()[1];
                isReverseMarker = true;
                break;
            default:
                return;
        }
        markerText[1] = entry.getTimeText();
        highlightPoint[0] = render.getHighlightPoint()[0];
        //标签位置区域计算
        for (AbsMarker<AbsRender<?, ?>> markerView : markerViewList) {
            isReverseMarker = !(markerView instanceof AxisTextMarker) || isReverseMarker;
            markerView.onMarkerViewMeasure(
                    chartModule.getRect(),
                    chartModule.getMatrix(),
                    highlightPoint[0],
                    highlightPoint[1],
                    markerText,
                    markerViewInfo,
                    isReverseMarker);
        }
        if (markerViewInfo[1] <= render.getTopModule().getRect().top) {
            top = markerViewInfo[3];
            bottom = render.getBottomModule().getRect().bottom;
        } else {
            top = render.getTopModule().getRect().top;
            bottom = markerViewInfo[1];
        }
        if (markerViewInfo[0] <= chartModule.getRect().left) {
            left = markerViewInfo[2];
            right = chartModule.getRect().right;
        } else {
            left = chartModule.getRect().left;
            right = markerViewInfo[0];
        }
        if (attribute.highLightStyle != HighLightStyle.NONE) {
            //设置高亮线宽度
            float pointWidth = render.getSubtractSpacePointWidth();
            axisHighlightPaint.setStrokeWidth(attribute.axisHighlightAutoWidth ? pointWidth : attribute.lineWidth);
            gridHighlightPaint.setStrokeWidth(attribute.gridHighlightAutoWidth ? pointWidth : attribute.lineWidth);
            //绘制axis高亮线
            highlightPath.moveTo(left, highlightPoint[1]);
            highlightPath.lineTo(right, highlightPoint[1]);
            canvas.drawPath(highlightPath, axisHighlightPaint);
            highlightPath.rewind();
            //绘制grid高亮线
            highlightPath.moveTo(highlightPoint[0], top);
            highlightPath.lineTo(highlightPoint[0], bottom);
            canvas.drawPath(highlightPath, gridHighlightPaint);
            highlightPath.rewind();
        }
        //绘制标签
        for (AbsMarker<AbsRender<?, ?>> markerView : markerViewList) {
            markerView.onMarkerViewDraw(canvas, markerText);
        }
    }
}
