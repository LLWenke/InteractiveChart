
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
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

import java.util.Arrays;

/**
 * Axis轴绘制组件
 * <p>AxisDrawing</p>
 */

public class AxisDrawing extends AbsDrawing<AbsRender<?, ?>, AbsModule<?>> {
    private static final String TAG = "AxisDrawing";
    private BaseAttribute attribute;

    private final TextPaint axisLabelPaintLeft = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Axis 轴标签文字的画笔(左)
    private final TextPaint axisLabelPaintRight = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Axis 轴标签文字的画笔（右）
    private final Paint axisLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Axis 轴线条的画笔
    private final Rect rect = new Rect();//用于测量文字的实际占用区域

    private final float[] pointCache = new float[2];
    private final float[] lineBuffer = new float[8];
    private final int axisCount;
    private final boolean isQuantization;
    private int axisStart = 0, axisEnd = 0;
    private float left, right, textCenter, regionHeight, unilateralTextOffset, lineOffset;

    /**
     * 构造
     *
     * @param axisCount      行数
     * @param isQuantization 是否量化数字
     */
    public AxisDrawing(int axisCount, boolean isQuantization) {
        this.axisCount = axisCount;
        this.isQuantization = isQuantization;
    }

    @Override
    public void onInit(AbsRender<?, ?> render, AbsModule<?> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        axisLabelPaintLeft.setTypeface(FontStyle.typeFace);
        axisLabelPaintLeft.setTextSize(attribute.labelSize);
        axisLabelPaintLeft.setColor(attribute.labelColor);

        axisLabelPaintRight.setTypeface(FontStyle.typeFace);
        axisLabelPaintRight.setTextSize(attribute.labelSize);
        axisLabelPaintRight.setColor(attribute.labelColor);
        axisLabelPaintRight.setTextAlign(Paint.Align.RIGHT);

        axisLinePaint.setStyle(Paint.Style.STROKE);
        axisLinePaint.setStrokeWidth(attribute.lineWidth);
        axisLinePaint.setColor(attribute.lineColor);

        Utils.measureTextArea(axisLabelPaintLeft, rect);
        axisStart = attribute.showFirstAxis ? 0 : 1;
        axisEnd = attribute.showLastAxis ? axisCount : axisCount - 1;
        textCenter = rect.height() / 2f;
        lineOffset = attribute.lineWidth / 2f;
        unilateralTextOffset = attribute.axisLineState ? -rect.top + attribute.axisLabelTBMargin + lineOffset : textCenter;
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {

    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        for (int i = axisStart; i < axisEnd; i++) {
            pointCache[1] = lineBuffer[1] = lineBuffer[3] = viewRect.top + i * regionHeight;
            render.invertMapPoints(pointCache);
            float value, offset;
            if (i == 0) {
                value = extremum[3];
                offset = lineOffset + attribute.axisLabelTBMargin + rect.height();
            } else if (i == axisCount - 1) {
                value = extremum[1];
                offset = -lineOffset - attribute.axisLabelTBMargin;
            } else {
                value = pointCache[1];
                offset = unilateralTextOffset;
            }
            String text;
            if (isQuantization) {
                text = render.getAdapter().rateQuantizationConversion(value, render.getAdapter().getScale().getQuoteScale(), false);
            } else {
                text = render.getAdapter().rateConversion(value, render.getAdapter().getScale().getQuoteScale(), false);
            }
            // 绘制横向网格线
            if (attribute.axisLabelLocation == AxisLabelLocation.ALL) {
                lineBuffer[5] = lineBuffer[7] = lineBuffer[1];
                lineBuffer[0] = viewRect.left;
                lineBuffer[2] = left;
                lineBuffer[4] = right;
                lineBuffer[6] = viewRect.right;
                offset = textCenter;
            } else {
                lineBuffer[0] = viewRect.left;
                lineBuffer[2] = viewRect.right;
            }
            if (attribute.axisLabelLocation == AxisLabelLocation.LEFT) {
                canvas.drawText(text, left, lineBuffer[1] + offset, axisLabelPaintLeft);
            } else if (attribute.axisLabelLocation == AxisLabelLocation.RIGHT) {
                canvas.drawText(text, right, lineBuffer[1] + offset, axisLabelPaintRight);
            } else {
                canvas.drawText(text, left, lineBuffer[1] + offset, axisLabelPaintLeft);
                canvas.drawText(text, right, lineBuffer[5] + offset, axisLabelPaintRight);
            }
            if (attribute.axisLineState) {
                canvas.drawLines(lineBuffer, axisLinePaint);
            }
        }
    }

    @Override
    public void drawOver(Canvas canvas) {

    }

    @Override
    public void onLayoutComplete() {
        regionHeight = viewRect.height() / (axisCount - 1);
        left = viewRect.left + attribute.axisLabelLRMargin;
        right = viewRect.right - attribute.axisLabelLRMargin;
        Arrays.fill(lineBuffer, 0f);
    }
}