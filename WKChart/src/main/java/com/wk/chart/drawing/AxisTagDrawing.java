
package com.wk.chart.drawing;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.ValueUtils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;


/**
 * <p>AxisTagDrawing</p>
 * Axis tag组件
 */

public class AxisTagDrawing extends AbsDrawing<AbsRender<?, ?>, AbsModule<AbsEntry>> {
    private BaseAttribute attribute;//配置文件

    private final TextPaint labelPaintLeft = new TextPaint(Paint.ANTI_ALIAS_FLAG);// 标签画笔
    private final TextPaint labelPaintRight = new TextPaint(Paint.ANTI_ALIAS_FLAG);// 标签画笔
    private final Rect rect = new Rect();//用于测量文字的实际占用区域
    private final float[] labelBuffer = new float[4];// 计算label坐标用的

    @Override
    public void onInit(AbsRender<?, ?> render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        labelPaintLeft.setTextSize(attribute.labelSize);
        labelPaintLeft.setColor(attribute.labelColor);
        labelPaintLeft.setTypeface(FontStyle.typeFace);

        labelPaintRight.setTextSize(attribute.labelSize);
        labelPaintRight.setColor(attribute.labelColor);
        labelPaintRight.setTypeface(FontStyle.typeFace);
        labelPaintRight.setTextAlign(Paint.Align.RIGHT);

        Utils.measureTextArea(labelPaintLeft, rect);
    }

    @Override
    public float[] onInitMargin() {
        float tagHeight = rect.height() + attribute.axisTagMarginY * 2f;
        switch (attribute.axisLabelLocation) {
            case LEFT:
            case RIGHT:
            case ALL:
                margin[1] = tagHeight;
                margin[3] = absChartModule.getModuleType() == ModuleType.VOLUME ? 0 : tagHeight;
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
        String topLabel, bottomLabel = null;
        switch (absChartModule.getModuleType()) {
            case ModuleType.VOLUME:
                topLabel = ValueUtils.formatBig(absChartModule.getMaxY().value,
                        render.getAdapter().getScale().getQuoteScale());
                break;
            default:
                topLabel = render.exchangeRateConversion(extremum[3],
                        render.getAdapter().getScale().getQuoteScale());
                bottomLabel = render.exchangeRateConversion(extremum[1],
                        render.getAdapter().getScale().getQuoteScale());
                break;
        }
        if (null != topLabel) {
            drawTagLabel(canvas, labelBuffer[1], topLabel);
        }
        if (null != bottomLabel) {
            drawTagLabel(canvas, labelBuffer[3], bottomLabel);
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
    public void onViewChange() {
        float top = viewRect.top, bottom = viewRect.bottom;
        switch (attribute.axisTagLocation) {
            case LEFT:
            case RIGHT:
            case ALL:
                top = viewRect.top - attribute.axisTagMarginY;
                bottom = viewRect.bottom + rect.height() + attribute.axisTagMarginY;
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
}
