
package com.wk.chart.drawing.depth;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.wk.chart.adapter.DepthAdapter;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.drawing.child.AxisTextMarker;
import com.wk.chart.drawing.child.GridTextMarker;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.entry.DepthEntry;
import com.wk.chart.enumeration.HighLightStyle;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.DepthRender;

/**
 * <p>DepthHighlightDrawing</p>
 */

public class DepthHighlightDrawing extends AbsDrawing<DepthRender, AbsModule<AbsEntry>> {
    private static final String TAG = "DepthHighlightDrawing";
    private DepthAttribute attribute;//配置文件
    private final AxisTextMarker axisTextMarker;//axis标签
    private final GridTextMarker gridTextMarker;//grid标签
    private final Paint bidHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 买单高亮线条画笔
    private final Paint askHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 卖单高亮线条画笔
    private final Paint bidCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//买单圆点画笔
    private final Paint askCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//卖单圆点画笔
    private final Path highlightPath = new Path();//高亮线绘制路径
    private final float[] highlightPoint = new float[2];//高亮线条x，y

    public DepthHighlightDrawing(AxisTextMarker axisTextMarker, GridTextMarker gridTextMarker) {
        this.axisTextMarker = axisTextMarker;
        this.gridTextMarker = gridTextMarker;
    }

    @Override
    public void onInit(DepthRender render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        bidHighlightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bidHighlightPaint.setColor(Utils.getColorWithAlpha(attribute.increasingColor
                , attribute.shaderBeginColorAlpha));
        bidHighlightPaint.setStrokeWidth(attribute.polylineWidth);

        askHighlightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        askHighlightPaint.setColor(Utils.getColorWithAlpha(attribute.decreasingColor
                , attribute.shaderBeginColorAlpha));
        askHighlightPaint.setStrokeWidth(attribute.polylineWidth);

        bidCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        bidCirclePaint.setColor(attribute.increasingColor);
        bidCirclePaint.setStrokeWidth(attribute.circleSize);

        askCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        askCirclePaint.setColor(attribute.decreasingColor);
        askCirclePaint.setStrokeWidth(attribute.circleSize);

        if (attribute.highLightStyle == HighLightStyle.DOTTED) {
            DashPathEffect dashPathEffect = new DashPathEffect(new float[]{10f, 5f}, 0);
            bidHighlightPaint.setPathEffect(dashPathEffect);
            askHighlightPaint.setPathEffect(dashPathEffect);
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

    @Override
    public void drawOver(Canvas canvas) {
        if (!render.isHighlight()) {
            return;
        }
        // 绘制高亮
        Paint circlePant;//圆点画笔
        Paint highlightPaint;//高亮线画笔
        float left, top, right, bottom;
        String axisMarkerText, gridMarkerText;
        float[] axisMarkerBuffer, gridMarkerBuffer;
        float highlightX = render.getHighlightPoint()[0];
        DepthEntry entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
        //获取当前焦点区域内的图表模型
        AbsModule<AbsEntry> focusModule = attribute.axisHighlightLabelAutoSelect ? render.getFocusModule() : render.getMainModule();
        //区分区域
        if (entry.getType() == DepthAdapter.BID) {
            circlePant = bidCirclePaint;
            highlightPaint = bidHighlightPaint;
        } else {
            circlePant = askCirclePaint;
            highlightPaint = askHighlightPaint;
        }
        axisMarkerText = entry.getTotalAmount().text;
        gridMarkerText = render.getAdapter().rateConversion(entry.getPrice(), false, false);
        highlightPoint[1] = entry.getTotalAmount().value;
        render.mapPoints(focusModule.getMatrix(), highlightPoint);
        highlightPoint[0] = highlightX;
        //axis标签位置区域计算
        float[] drawingNonOverlapMargin = focusModule.getDrawingNonOverlapMargin();
        RectF rect = focusModule.getRect();
        if (null == axisTextMarker) {
            left = rect.left;
            right = rect.right;
        } else {
            axisMarkerBuffer = axisTextMarker.onMeasureChildView(rect, drawingNonOverlapMargin,
                    highlightPoint[0], highlightPoint[1], axisMarkerText, true);
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
            highlightPaint.setStrokeWidth(attribute.axisHighlightAutoWidth ? pointWidth : attribute.lineWidth);
            //绘制axis高亮线
            highlightPath.moveTo(left, highlightPoint[1]);
            highlightPath.lineTo(right, highlightPoint[1]);
            canvas.drawPath(highlightPath, highlightPaint);
            highlightPath.rewind();
            //绘制grid高亮线
            highlightPath.moveTo(highlightPoint[0], top);
            highlightPath.lineTo(highlightPoint[0], bottom);
            canvas.drawPath(highlightPath, highlightPaint);
            highlightPath.rewind();
        }
        //绘制圆点
        canvas.drawCircle(render.getHighlightPoint()[0], render.getHighlightPoint()[1],
                attribute.circleSize / 2f, circlePant);
        //绘制标签
        if (null != axisTextMarker) {
            axisTextMarker.onChildViewDraw(canvas, axisMarkerText);
        }
        if (null != gridTextMarker) {
            gridTextMarker.onChildViewDraw(canvas, gridMarkerText);
        }
    }
}
