
package com.wk.chart.drawing;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.AxisTagVisible;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

import static com.wk.chart.enumeration.AxisTagLocation.ALL;
import static com.wk.chart.enumeration.AxisTagLocation.LEFT;
import static com.wk.chart.enumeration.AxisTagLocation.RIGHT;


/**
 * <p>AxisTagDrawing</p>
 * Axis tag组件
 */

public class AxisTagDrawing extends AbsDrawing<AbsRender<?, ?>, AbsModule<AbsEntry>> {
    private BaseAttribute attribute;//配置文件
    private final int axisTagVisible;//横向标签显示方式
    private final TextPaint labelPaintLeft = new TextPaint(Paint.ANTI_ALIAS_FLAG);// 标签画笔
    private final TextPaint labelPaintRight = new TextPaint(Paint.ANTI_ALIAS_FLAG);// 标签画笔
    private final Paint axisLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Axis 轴线条的画笔
    private final Rect rect = new Rect();//用于测量文字的实际占用区域
    private final float[] labelBuffer = new float[4];// label坐标
    private final float[] lineBuffer = new float[16];// 线条坐标
    private float textHalfHeight;//文字

    public AxisTagDrawing(int axisTagVisible) {
        this.axisTagVisible = axisTagVisible;
    }

    @Override
    public void onInit(AbsRender<?, ?> render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        axisLinePaint.setStyle(Paint.Style.STROKE);
        axisLinePaint.setStrokeWidth(attribute.lineWidth);
        axisLinePaint.setColor(attribute.lineColor);

        labelPaintLeft.setTextSize(attribute.labelSize);
        labelPaintLeft.setColor(attribute.labelColor);
        labelPaintLeft.setTypeface(FontStyle.typeFace);

        labelPaintRight.setTextSize(attribute.labelSize);
        labelPaintRight.setColor(attribute.labelColor);
        labelPaintRight.setTypeface(FontStyle.typeFace);
        labelPaintRight.setTextAlign(Paint.Align.RIGHT);

        Utils.measureTextArea(labelPaintLeft, rect);

        textHalfHeight = rect.height() / 2f;
    }

    @Override
    public float[] onInitMargin() {
        if (attribute.axisTagLocation == LEFT || attribute.axisTagLocation == RIGHT || attribute.axisTagLocation == ALL) {
            float tagHeight = rect.height() + attribute.axisTagMarginY * 2f;
            if ((axisTagVisible & AxisTagVisible.TOP_VISIBLE) != 0) {//上标签可见
                margin[1] = tagHeight;
            }
            if ((axisTagVisible & AxisTagVisible.BOTTOM_VISIBLE) != 0) {//下标签可见
                margin[3] = tagHeight;
            }
        }
        return margin;
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {

    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        String topLabel = null, bottomLabel = null;
        if ((axisTagVisible & AxisTagVisible.TOP_VISIBLE) != 0) {//上标签可见
            topLabel = getLabelText(extremum[3]);
        }
        if ((axisTagVisible & AxisTagVisible.BOTTOM_VISIBLE) != 0) {//下标签可见
            bottomLabel = getLabelText(extremum[1]);
        }
        if (null != topLabel) {
            lineBuffer[1] = lineBuffer[7] = labelBuffer[1];
            lineBuffer[3] = lineBuffer[5] = labelBuffer[1] + textHalfHeight;
            lineBuffer[0] = viewRect.left;
            lineBuffer[2] = viewRect.left + attribute.axisTagMarginX;
            lineBuffer[4] = viewRect.right - attribute.axisTagMarginX;
            lineBuffer[6] = viewRect.right;
            drawTagLabel(canvas, labelBuffer[1], topLabel);
        }
        if (null != bottomLabel) {
            lineBuffer[9] = lineBuffer[15] = labelBuffer[3];
            lineBuffer[11] = lineBuffer[13] = labelBuffer[3] - textHalfHeight;
            lineBuffer[8] = viewRect.left;
            lineBuffer[10] = viewRect.left + attribute.axisTagMarginX;
            lineBuffer[12] = viewRect.right - attribute.axisTagMarginX;
            lineBuffer[14] = viewRect.right;
            drawTagLabel(canvas, labelBuffer[3], bottomLabel);
        }
        if (attribute.axisLineState) {
            canvas.drawLines(lineBuffer, axisLinePaint);
        }
    }

    private void drawTagLabel(Canvas canvas, float labelY, String label) {
        switch (attribute.axisTagLocation) {
            case LEFT:
            case LEFT_INSIDE:
                canvas.drawText(label, labelBuffer[0], labelY, labelPaintLeft);
                break;
            case RIGHT:
            case RIGHT_INSIDE:
                canvas.drawText(label, labelBuffer[2], labelY, labelPaintRight);
                break;
            case ALL:
            case ALL_INSIDE:
                canvas.drawText(label, labelBuffer[0], labelY, labelPaintLeft);
                canvas.drawText(label, labelBuffer[2], labelY, labelPaintRight);
                break;
        }
    }

    @Override
    public void drawOver(Canvas canvas) {
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onLayoutComplete() {
        float top = viewRect.top, bottom = viewRect.bottom;
        switch (attribute.axisTagLocation) {
            case LEFT:
            case RIGHT:
            case ALL:
                top = viewRect.top - attribute.axisTagMarginY - attribute.borderWidth;
                bottom = viewRect.bottom + rect.height() + attribute.axisTagMarginY + attribute.borderWidth;
                break;
            case LEFT_INSIDE:
            case RIGHT_INSIDE:
            case ALL_INSIDE:
                top = viewRect.top + rect.height() + attribute.axisTagMarginY;
                bottom = viewRect.bottom - attribute.axisTagMarginY;
                break;
        }
        labelBuffer[0] = viewRect.left + attribute.axisTagMarginX;
        labelBuffer[1] = top;
        labelBuffer[2] = viewRect.right - attribute.axisTagMarginX;
        labelBuffer[3] = bottom;
    }

    /**
     * 格式话标签文字
     */
    private String getLabelText(float value) {
        if (absChartModule.getModuleType() == ModuleType.VOLUME) {
            return render.getAdapter().quantizationConversion(absChartModule.getMaxY(), true);
        }
        return render.getAdapter().rateConversion(value, render.getAdapter().getScale().getQuoteScale(), false, false);
    }
}
