
package com.wk.chart.drawing;


import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.LineStyle;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * <p>Axis轴组件</p>
 */

public class AxisDrawing extends AbsDrawing<AbsRender<?, ?>, AbsModule<?>> {
    private static final String TAG = "AxisDrawing";
    private BaseAttribute attribute;

    private final TextPaint axisLabelPaintLeft = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Axis 轴标签文字的画笔(左)
    private final TextPaint axisLabelPaintRight = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Axis 轴标签文字的画笔（右）
    private final Paint axisLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Axis 轴线条的画笔
    private final Rect rect = new Rect();//用于测量文字的实际占用区域

    private final float[] pointCache = new float[2];//[x,y]
    private final float[] labelBuffer = new float[4];//[x1,y1,x2,y2]
    private final float[] lineBuffer = new float[8];//[x1,y1,x2,y2,x3,y4,x4,y4]
    private final boolean isQuantization;
    private final int axisCount;
    private int axisStart = 0, axisEnd = 0;
    private float textHeight, textCenter, regionHeight, sortLineOffset, lineHeightOffset;

    /**
     * 构造
     *
     * @param axisCount      行数
     * @param isQuantization 是否量化数字
     */
    public AxisDrawing(int axisCount, boolean isQuantization) {
        this.isQuantization = isQuantization;
        this.axisCount = axisCount;
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
        axisStart = attribute.axisShowFirst ? 0 : 1;
        axisEnd = attribute.axisShowLast ? axisCount : axisCount - 1;
        lineHeightOffset = attribute.lineWidth / 2f;
        textHeight = rect.height();
        textCenter = textHeight / 2f;
        switch (attribute.axisLineStyle) {
            case SOLID:
                sortLineOffset = 0;
                axisLinePaint.setPathEffect(null);
                break;
            case DOTTED:
                sortLineOffset = 0;
                axisLinePaint.setPathEffect(new DashPathEffect(new float[]{10f, 5f}, 0));
                break;
            case SCALE_INSIDE:
            case SCALE_OUTSIDE:
                sortLineOffset = attribute.axisScaleLineLength;
                break;
        }
    }

    @Override
    public float[] onInitMargin(float viewWidth, float viewHeight) {
        float height = (float) Math.ceil(textHeight + attribute.axisLabelMarginVertical * 2f);
        if (attribute.axisShowFirst && (attribute.axisLabelPosition & PositionType.TOP) != 0) {//第一条可见
            margin[1] = height;
        }
        if (attribute.axisShowLast && (attribute.axisLabelPosition & PositionType.BOTTOM) != 0) {//最后一条可见
            margin[3] = height;
        }
        return margin;
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        for (int i = axisStart; i < axisEnd; i++) {
            String text;
            float labelOffset, lineOffset, axisY;
            axisY = viewRect.top + i * regionHeight;
            pointCache[1] = axisY;
            render.invertMapPoints(chartModule.getMatrix(), pointCache);
            if (i == 0) {
                labelOffset = textHeight;
                lineOffset = textCenter;
                text = getScaleLabel(extremum[3], chartModule.getMaxY());
            } else if (i == axisCount - 1) {
                labelOffset = 0;
                lineOffset = -textCenter;
                text = getScaleLabel(extremum[1], chartModule.getMinY());
            } else {
                labelOffset = textCenter;
                lineOffset = 0;
                text = getScaleLabel(pointCache[1], null);
            }
            //绘制刻度值
            if ((attribute.axisLabelPosition & PositionType.BOTTOM) != 0) {
                labelBuffer[1] = axisY + textHeight + lineHeightOffset + attribute.axisLabelMarginVertical;
                labelBuffer[3] = labelBuffer[1];
            } else if ((attribute.axisLabelPosition & PositionType.TOP) != 0) {
                labelBuffer[1] = axisY - lineHeightOffset - attribute.axisLabelMarginVertical;
                labelBuffer[3] = labelBuffer[1];
            } else {
                labelBuffer[1] = axisY + labelOffset + attribute.axisLabelMarginVertical;
                labelBuffer[3] = labelBuffer[1];
            }
            if ((attribute.axisLabelPosition & PositionType.START_AND_END) != 0) {
                labelBuffer[0] = viewRect.left + attribute.axisLabelMarginHorizontal + sortLineOffset;
                labelBuffer[2] = viewRect.right - attribute.axisLabelMarginHorizontal - sortLineOffset;
                canvas.drawText(text, labelBuffer[0], labelBuffer[1], axisLabelPaintLeft);
                canvas.drawText(text, labelBuffer[2], labelBuffer[3], axisLabelPaintRight);
            } else if ((attribute.axisLabelPosition & PositionType.END) != 0) {
                labelBuffer[2] = viewRect.right - attribute.axisLabelMarginHorizontal - sortLineOffset;
                canvas.drawText(text, labelBuffer[2], labelBuffer[3], axisLabelPaintRight);
            } else {
                labelBuffer[0] = viewRect.left + attribute.axisLabelMarginHorizontal + sortLineOffset;
                canvas.drawText(text, labelBuffer[0], labelBuffer[1], axisLabelPaintLeft);
            }
            // 绘制横刻度线
            if (attribute.axisLineStyle == LineStyle.SCALE_OUTSIDE
                    || attribute.axisLineStyle == LineStyle.SCALE_INSIDE) {
                lineBuffer[1] = lineBuffer[5] = axisY;
                lineBuffer[3] = lineBuffer[7] = axisY + lineOffset;
                lineBuffer[0] = viewRect.left;
                lineBuffer[2] = viewRect.left + sortLineOffset;
                lineBuffer[4] = viewRect.right;
                lineBuffer[6] = viewRect.right - sortLineOffset;
                canvas.drawLines(lineBuffer, axisLinePaint);
            } else if (attribute.axisLineStyle == LineStyle.DOTTED
                    || attribute.axisLineStyle == LineStyle.SOLID) {
                lineBuffer[1] = lineBuffer[3] = lineBuffer[5] = lineBuffer[7] = axisY;
                lineBuffer[0] = viewRect.left;
                lineBuffer[2] = viewRect.right;
                lineBuffer[4] = 0;
                lineBuffer[6] = 0;
                canvas.drawLines(lineBuffer, axisLinePaint);
            }
        }
    }

    @Override
    public void onLayoutComplete() {
        super.onLayoutComplete();
        regionHeight = viewRect.height() / (float) (axisCount - 1);
        Arrays.fill(lineBuffer, 0f);
        Arrays.fill(labelBuffer, 0f);
    }

    /**
     * 格式化刻度标签
     */
    private String getScaleLabel(float value, @Nullable ValueEntry entry) {
        if (null != entry && chartModule.getModuleType() == ModuleType.VOLUME) {
            return render.getAdapter().quantizationConversion(entry, true);
        } else if (isQuantization) {
            return render.getAdapter().rateConversion(value, render.getAdapter().getScale().getQuoteScale(), true, false);
        } else {
            return render.getAdapter().rateConversion(value, render.getAdapter().getScale().getQuoteScale(), false, false);
        }
    }
}