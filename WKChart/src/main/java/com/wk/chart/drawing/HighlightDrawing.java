
package com.wk.chart.drawing;


import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.drawing.child.AxisTextMarker;
import com.wk.chart.drawing.child.GridTextMarker;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.enumeration.HighLightStyle;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.CandleRender;

/**
 * <p>HighlightDrawing</p>
 */

public class HighlightDrawing extends AbsDrawing<CandleRender, AbsModule<AbsEntry>> {
    private static final String TAG = "HighlightDrawing";
    private CandleAttribute attribute;//配置文件
    private final AxisTextMarker axisTextMarker;//axis标签
    private final GridTextMarker gridTextMarker;//grid标签
    private final Paint axisHighlightPaint = new Paint(); //axis高亮线条画笔
    private final Paint gridHighlightPaint = new Paint(); //grid高亮线条画笔
    private final Path highlightPath = new Path();//高亮线绘制路径
    private final float[] highlightPoint = new float[2];//高亮线条x，y

    public HighlightDrawing(AxisTextMarker axisTextMarker, GridTextMarker gridTextMarker) {
        this.axisTextMarker = axisTextMarker;
        this.gridTextMarker = gridTextMarker;
    }

    @Override
    public void onInit(CandleRender render, AbsModule<AbsEntry> chartModule) {
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
        if (null != axisTextMarker) {
            axisTextMarker.onInit(render, chartModule);
        }
        if (null != gridTextMarker) {
            gridTextMarker.onInit(render, chartModule);
        }
    }

    @Override
    public float[] onInitMargin(float viewWidth, float viewHeight) {
        if (null != axisTextMarker) {
            float[] markerMargin = axisTextMarker.onInitMargin(viewWidth, viewHeight);
            margin[0] = Math.max(margin[0], markerMargin[0]);
            margin[1] = Math.max(margin[1], markerMargin[1]);
            margin[2] = Math.max(margin[2], markerMargin[2]);
            margin[3] = Math.max(margin[3], markerMargin[3]);
        }
        if (null != gridTextMarker) {
            float[] markerMargin = gridTextMarker.onInitMargin(viewWidth, viewHeight);
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
        boolean isReverseMarker;
        float left, top, right, bottom;
        String axisMarkerText, gridMarkerText;
        float[] axisMarkerBuffer, gridMarkerBuffer;
        float highlightX = render.getHighlightPoint()[0];
        float highlightY = render.getHighlightPoint()[1];
        CandleEntry entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
        //获取当前焦点区域内的图表模型
        AbsModule<AbsEntry> focusModule = attribute.axisHighlightLabelAutoSelect ? render.getFocusModule() : render.getMainModule();
        //获取高亮点的值
        switch (focusModule.getModuleType()) {
            case ModuleType.CANDLE://k线图 指标
            case ModuleType.TIME://分时图 指标
                highlightPoint[1] = entry.getClose().value;
                render.mapPoints(focusModule.getMatrix(), highlightPoint);
                axisMarkerText = render.getAdapter().rateConversion(entry.getClose(), false, false);
                isReverseMarker = false;
                break;
            case ModuleType.VOLUME://交易量 指标
                highlightPoint[1] = entry.getVolume().value;
                render.mapPoints(focusModule.getMatrix(), highlightPoint);
                axisMarkerText = render.getAdapter().quantizationConversion(entry.getVolume(), true);
                isReverseMarker = true;
                break;
            case ModuleType.MUTATION:  // 指标
                highlightPoint[1] = highlightY;
                render.invertMapPoints(focusModule.getMatrix(), highlightPoint);
                axisMarkerText = render.getAdapter().rateConversion(highlightPoint[1],
                        render.getAdapter().getScale().getQuoteScale(), false, false);
                highlightPoint[1] = highlightY;
                isReverseMarker = true;
                break;
            default:
                return;
        }

        gridMarkerText = entry.getTimeText();
        highlightPoint[0] = highlightX;
        //axis标签位置区域计算
        float[] drawingNonOverlapMargin = focusModule.getDrawingNonOverlapMargin();
        RectF rect = focusModule.getRect();
        if (null == axisTextMarker) {
            left = rect.left;
            right = rect.right;
        } else {
            axisMarkerBuffer = axisTextMarker.onMeasureChildView(rect, drawingNonOverlapMargin,
                    highlightPoint[0], highlightPoint[1], axisMarkerText, isReverseMarker);
            if (axisMarkerBuffer[0] < (rect.left + rect.width() / 2f)) {
                left = axisMarkerBuffer[2];
                right = rect.right;
            } else {
                left = rect.left;
                right = axisMarkerBuffer[0];
            }
        }
        //grid标签位置区域计算
        drawingNonOverlapMargin = absChartModule.getDrawingNonOverlapMargin();
        rect = viewRect;
        if (null == gridTextMarker) {
            top = rect.top;
            bottom = rect.bottom;
        } else {
            gridMarkerBuffer = gridTextMarker.onMeasureChildView(rect, drawingNonOverlapMargin,
                    highlightPoint[0], highlightPoint[1], gridMarkerText, true);
            if (gridMarkerBuffer[1] < (rect.top + rect.height() / 2f)) {
                top = gridMarkerBuffer[3];
                bottom = rect.bottom;
            } else {
                top = rect.top;
                bottom = gridMarkerBuffer[1];
            }
        }
        //绘制高亮线
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
        if (null != axisTextMarker) {
            axisTextMarker.onChildViewDraw(canvas, axisMarkerText);
        }
        if (null != gridTextMarker) {
            gridTextMarker.onChildViewDraw(canvas, gridMarkerText);
        }
    }
}
