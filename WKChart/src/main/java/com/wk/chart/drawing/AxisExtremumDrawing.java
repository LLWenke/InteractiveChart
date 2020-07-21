
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.ValueUtils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.module.base.AbsChartModule;
import com.wk.chart.render.AbsRender;


/**
 * <p>AxisExtremumDrawing</p>
 * Axis极值组件
 */

public class AxisExtremumDrawing extends AbsDrawing<AbsRender, AbsChartModule> {
    private BaseAttribute attribute;//配置文件

    private TextPaint labelPaintLeft = new TextPaint(Paint.ANTI_ALIAS_FLAG);// 标签画笔
    private TextPaint labelPaintRight = new TextPaint(Paint.ANTI_ALIAS_FLAG);// 标签画笔
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 标线画笔
    private Rect rect = new Rect();//用于测量文字的实际占用区域
    private float[] labelBuffer = new float[4];// 计算label坐标用的
    private float[] lineBuffer;// 计算横线线坐标用的

    @Override
    public void onInit(AbsRender render, AbsChartModule chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        labelPaintLeft.setTextSize(attribute.labelSize);
        labelPaintLeft.setColor(attribute.labelColor);
        labelPaintLeft.setTypeface(FontStyle.typeFace);

        labelPaintRight.setTextSize(attribute.labelSize);
        labelPaintRight.setColor(attribute.labelColor);
        labelPaintRight.setTextAlign(Paint.Align.RIGHT);

        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(attribute.lineWidth);
        linePaint.setColor(attribute.lineColor);

        Utils.measureTextArea(labelPaintLeft, rect);
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {

    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        String topLabel, bottomLabel = null;
        switch (absChartModule.getModuleType()) {
            case VOLUME:
                topLabel = ValueUtils.formatBig(absChartModule.getMaxY().value,
                        render.getAdapter().getScale().getBaseScale());
                break;
            case CANDLE:
            case DEPTH:
                topLabel = absChartModule.getMaxY().text;
                bottomLabel = absChartModule.getMinY().text;
                break;
            default:
                topLabel = render.exchangeRateConversion(extremum[3],
                        render.getAdapter().getScale().getBaseScale());
                bottomLabel = render.exchangeRateConversion(extremum[1],
                        render.getAdapter().getScale().getBaseScale());
                break;
        }
        if (null != topLabel) {
            drawExtremumLabel(canvas, labelBuffer[1], topLabel);
        }
        if (null != bottomLabel) {
            drawExtremumLabel(canvas, labelBuffer[3], bottomLabel);
        }
        if (attribute.extremumLineState) {
            canvas.drawLines(lineBuffer, linePaint);
        }
    }

    private void drawExtremumLabel(Canvas canvas, float labelY, String label) {
        switch (attribute.axisLabelLocation) {
            case LEFT:
                canvas.drawText(label, labelBuffer[0], labelY, labelPaintLeft);
                break;
            case RIGHT:
                canvas.drawText(label, labelBuffer[2], labelY, labelPaintRight);
                break;
            case ALL:
                canvas.drawText(label, labelBuffer[0], labelY, labelPaintLeft);
                canvas.drawText(label, labelBuffer[2], labelY, labelPaintRight);
                break;
        }
    }

    @Override
    public void drawOver(Canvas canvas) {

    }

    @Override
    public void onViewChange() {
        //设置横线坐标（共两条，top和bottom）
        switch (absChartModule.getModuleType()) {
            case VOLUME:
            case CANDLE:
                lineBuffer = new float[4];
                lineBuffer[0] = viewRect.left;
                lineBuffer[1] = viewRect.top;
                lineBuffer[2] = viewRect.right;
                lineBuffer[3] = viewRect.top;
                break;
            default:
                lineBuffer = new float[8];
                lineBuffer[0] = viewRect.left;
                lineBuffer[1] = viewRect.top;
                lineBuffer[2] = viewRect.right;
                lineBuffer[3] = viewRect.top;
                lineBuffer[4] = viewRect.left;
                lineBuffer[5] = viewRect.bottom;
                lineBuffer[6] = viewRect.right;
                lineBuffer[7] = viewRect.bottom;
                break;
        }
        labelBuffer[0] = viewRect.left + attribute.axisLabelLRMargin;
        labelBuffer[1] = viewRect.top + rect.height() + attribute.axisLabelTBMargin;
        labelBuffer[2] = viewRect.right - attribute.axisLabelLRMargin;
        labelBuffer[3] = viewRect.bottom - attribute.axisLabelTBMargin;
    }
}
