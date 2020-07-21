
package com.wk.chart.drawing;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.enumeration.AxisLabelLocation;
import com.wk.chart.module.base.AbsChartModule;
import com.wk.chart.render.AbsRender;

/**
 * Axis轴绘制组件
 * <p>AxisDrawing</p>
 */

public class AxisDrawing extends AbsDrawing<AbsRender, AbsChartModule> {
    private static final String TAG = "AxisDrawing";
    private BaseAttribute attribute;

    private TextPaint axisLabelPaintLeft = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Axis 轴标签文字的画笔(左)
    private TextPaint axisLabelPaintRight = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Axis 轴标签文字的画笔（右）
    private Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Axis 轴的画笔
    private Rect rect = new Rect();//用于测量文字的实际占用区域

    private final float[] pointCache = new float[2];
    private float[] lineBuffer = new float[8];

    private float left, right, textCenter, regionHeight;

    @Override
    public void onInit(AbsRender render, AbsChartModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        axisLabelPaintLeft.setTypeface(FontStyle.typeFace);
        axisLabelPaintLeft.setTextSize(attribute.labelSize);
        axisLabelPaintLeft.setColor(attribute.labelColor);

        axisLabelPaintRight.setTypeface(FontStyle.typeFace);
        axisLabelPaintRight.setTextSize(attribute.labelSize);
        axisLabelPaintRight.setColor(attribute.labelColor);
        axisLabelPaintRight.setTextAlign(Paint.Align.RIGHT);

        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(attribute.lineWidth);
        axisPaint.setColor(attribute.lineColor);

        Utils.measureTextArea(axisLabelPaintLeft, rect);
        textCenter = rect.height() / 2f;
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {

    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        for (int i = 1; i < attribute.axisCount; i++) {
            pointCache[1] = lineBuffer[1] = lineBuffer[3] = viewRect.top + i * regionHeight;
            render.invertMapPoints(pointCache);
            String text = render.exchangeRateConversion(pointCache[1],
                    render.getAdapter().getScale().getBaseScale());
            // 绘制横向网格线
            if (attribute.axisLabelLocation == AxisLabelLocation.ALL) {
                lineBuffer[5] = lineBuffer[7] = lineBuffer[1];
                lineBuffer[0] = viewRect.left;
                lineBuffer[2] = left - attribute.axisLabelLRMargin;
                lineBuffer[4] = right + attribute.axisLabelLRMargin;
                lineBuffer[6] = viewRect.right;
                canvas.drawText(text, left, lineBuffer[1] + textCenter, axisLabelPaintLeft);
                canvas.drawText(text, right, lineBuffer[5] + textCenter, axisLabelPaintRight);
            } else {
                lineBuffer[0] = viewRect.left;
                lineBuffer[2] = viewRect.right;
                if (attribute.axisLabelLocation == AxisLabelLocation.LEFT) {
                    canvas.drawText(text, left, lineBuffer[1] - rect.top + attribute.axisLabelTBMargin,
                            axisLabelPaintLeft);
                } else {
                    canvas.drawText(text, right, lineBuffer[1] - rect.top + attribute.axisLabelTBMargin,
                            axisLabelPaintRight);
                }
            }
            if (attribute.axisLineState) {
                canvas.drawLine(lineBuffer[0], lineBuffer[1], lineBuffer[2], lineBuffer[3], axisPaint);
            }
        }
    }

    @Override
    public void drawOver(Canvas canvas) {

    }

    @Override
    public void onViewChange() {
        regionHeight = viewRect.height() / attribute.axisCount;
        left = viewRect.left + attribute.axisLabelLRMargin;
        right = viewRect.right - attribute.axisLabelLRMargin;
    }
}