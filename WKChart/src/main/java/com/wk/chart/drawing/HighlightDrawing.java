
package com.wk.chart.drawing;


import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.wk.chart.compat.DisplayTypeUtils;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.HighLightStyle;
import com.wk.chart.marker.AbsMarker;
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

    private final Paint xHighlightPaint = new Paint(); // X高亮线条画笔
    private final Paint yHighlightPaint = new Paint(); // Y高亮线条画笔
    private final Path highlightPath = new Path();

    private final float[] markerViewInfo = new float[4];//markerView的left,top,right,bottom信息
    private final float[] highlightPoint = new float[2];//高亮线条x，y
    private final float[] highlightInvertPoint = new float[2];//高亮线条坐标反转后的x，y
    private final String[] markerText = new String[2];//marker中显示的值
    private final List<AbsMarker<AbsRender<?, ?>>> markerViewList = new ArrayList<>();

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

        xHighlightPaint.setStyle(Paint.Style.STROKE);
        xHighlightPaint.setColor(attribute.xHighlightColor);

        yHighlightPaint.setStyle(Paint.Style.STROKE);
        yHighlightPaint.setColor(attribute.yHighlightColor);

        if (attribute.highLightStyle == HighLightStyle.DOTTED) {
            DashPathEffect dashPathEffect = new DashPathEffect(new float[]{10f, 5f}, 0);
            xHighlightPaint.setPathEffect(dashPathEffect);
            yHighlightPaint.setPathEffect(dashPathEffect);
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

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
    }

    @Override
    public void drawOver(Canvas canvas) {
        if (!render.isHighlight()) {
            return;
        }
        // 绘制高亮
        float left, top, right, bottom;

        CandleEntry entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
        //获取当前焦点区域内的chartModule
        AbsModule<? extends AbsEntry> chartModule = render.getModuleInFocusArea();
        if (null == chartModule) {
            return;
        }
        markerText[0] = DisplayTypeUtils.selectorFormat(entry.getTime(),
                render.getAdapter().getTimeType());
        //switch (chartModule.getInstance()) {
        //  case VOLUME://交易量 指标
        //    chartModule = render.getMainChartModule();
        //  case CANDLE://k线图 指标
        //  case TIME://分时图 指标
        //    highlightPoint[1] = entry.getClose().value;
        //    render.mapPoints(chartModule.getMatrix(), highlightPoint);
        //    highlightPoint[0] = render.getHighlightPoint()[0];
        //    markerText[1] = entry.getClose().text;
        //    break;
        //  case MACD:  //MACD 指标
        //  case KDJ:  //KDJ 指标
        //  case RSI:  //RSI 指标
        //  case BOLL:  //BOLL 指标
        //  default:
        //    highlightPoint[0] = render.getHighlightPoint()[0];
        //    highlightPoint[1] = render.getHighlightPoint()[1];
        //    markerText[1] = getInvertPoint(chartModule);
        //    break;
        //}
        chartModule = render.getMainModule();
        highlightPoint[1] = entry.getClose().value;
        render.mapPoints(chartModule.getMatrix(), highlightPoint);
        highlightPoint[0] = render.getHighlightPoint()[0];
        markerText[1] = render.getAdapter().rateConversion(entry.getClose().value,
                render.getAdapter().getScale().getQuoteScale(), true);

        for (AbsMarker<AbsRender<?, ?>> markerView : markerViewList) {
            markerView.onMarkerViewMeasure(chartModule.getRect(),
                    chartModule.getMatrix(),
                    highlightPoint[0],
                    highlightPoint[1],
                    markerText,
                    markerViewInfo);
        }
        RectF focusRect;
        switch (attribute.gridMarkerAlign) {
            case TOP_INSIDE:
            case TOP:
                focusRect = render.getTopModule().getRect();
                break;
            case BOTTOM:
            case BOTTOM_INSIDE:
                focusRect = render.getBottomModule().getRect();
                break;
            default:
                focusRect = chartModule.getRect();
                break;
        }
        if (markerViewInfo[1] < focusRect.top + focusRect.height() / 2) {
            top = markerViewInfo[3] > 0 ? markerViewInfo[3] : focusRect.top;
            bottom = focusRect.bottom;
        } else {
            top = focusRect.top;
            bottom = markerViewInfo[1] > 0 ? markerViewInfo[1] : focusRect.bottom;
        }
        if (markerViewInfo[0] < focusRect.left + focusRect.width() / 2) {
            left = markerViewInfo[2] > 0 ? markerViewInfo[2] : focusRect.left;
            right = focusRect.right;
        } else {
            left = focusRect.left;
            right = markerViewInfo[0] > 0 ? markerViewInfo[0] : focusRect.right;
        }
        float pointWidth = render.getSubtractSpacePointWidth();
        xHighlightPaint.setStrokeWidth(attribute.xHighlightAutoWidth ? pointWidth : attribute.lineWidth);
        if (attribute.yHighlightAutoDivision) {
            float[] highlightPts = render.buildViewLRCoordinates(highlightPoint[0],
                    highlightPoint[0], top, bottom, focusRect);
            for (int i = 3; i < highlightPts.length; i += 4) {
                highlightPath.moveTo(highlightPts[i - 3], highlightPts[i - 2]);
                highlightPath.lineTo(highlightPts[i - 1], highlightPts[i]);
            }
        } else {
            highlightPath.moveTo(highlightPoint[0], render.getTopModule().getRect().top);
            highlightPath.lineTo(highlightPoint[0], render.getBottomModule().getRect().bottom);
        }
        canvas.drawPath(highlightPath, xHighlightPaint);
        highlightPath.rewind();

        yHighlightPaint.setStrokeWidth(attribute.yHighlightAutoWidth ? pointWidth : attribute.lineWidth);

        highlightPath.moveTo(left, highlightPoint[1]);
        highlightPath.lineTo(right, highlightPoint[1]);
        canvas.drawPath(highlightPath, yHighlightPaint);
        highlightPath.rewind();

        for (AbsMarker<AbsRender<?, ?>> markerView : markerViewList) {
            markerView.onMarkerViewDraw(canvas, markerText);
        }
    }
}
