
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
import com.wk.chart.entry.ValueEntry;
import com.wk.chart.enumeration.ExtremumVisible;
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

/**
 * <p>AxisTagDrawing</p>
 * Axis tag组件
 */

public class ExtremumLabelDrawing extends AbsDrawing<AbsRender<?, ?>, AbsModule<AbsEntry>> {
    private BaseAttribute attribute;//配置文件
    private final int extremumLabelVisible;//极值标签显示状态式
    private final boolean isQuantization;//是否进行量化
    private final boolean isRate;//是否进行汇率转换
    private final TextPaint labelPaintLeft = new TextPaint(Paint.ANTI_ALIAS_FLAG);// 标签画笔
    private final TextPaint labelPaintRight = new TextPaint(Paint.ANTI_ALIAS_FLAG);// 标签画笔
    private final Paint axisLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Axis 轴线条的画笔
    private final Rect rect = new Rect();//用于测量文字的实际占用区域
    private final float[] labelBuffer = new float[8];// label坐标
    private float textHeight;//文字
    private boolean showMaxLabel, showMinLabel;//显示最大标签，显示最小标签

    public ExtremumLabelDrawing(int extremumLabelVisible, boolean isQuantization, boolean isRate) {
        this.extremumLabelVisible = extremumLabelVisible;
        this.isQuantization = isQuantization;
        this.isRate = isRate;
    }

    @Override
    public void onInit(AbsRender<?, ?> render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();
        showMaxLabel = (extremumLabelVisible & ExtremumVisible.MAX_VISIBLE) != 0;
        showMinLabel = (extremumLabelVisible & ExtremumVisible.MIN_VISIBLE) != 0;

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

        textHeight = rect.height();
    }

    @Override
    public float[] onInitMargin(float viewWidth, float viewHeight) {
        if ((attribute.extremumLabelPosition & PositionType.OUTSIDE_VERTICAL) != 0) {
            float labelHeight = (float) Math.ceil(textHeight + attribute.extremumLabelMarginVertical * 2f);
            if (showMaxLabel) {//上标签可见
                margin[1] = labelHeight;
            }
            if (showMinLabel) {//下标签可见
                margin[3] = labelHeight;
            }
        }
        return margin;
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        String topLabel = null, bottomLabel = null;
        if (showMaxLabel) {//最大值标签可见
            topLabel = getLabelText(absChartModule.getMaxY());
        }
        if (showMinLabel) {//最小值标签可见
            bottomLabel = getLabelText(absChartModule.getMinY());
        }
        if ((attribute.extremumLabelPosition & PositionType.START_AND_END) != 0) {
            if (showMaxLabel) {
                canvas.drawText(topLabel, labelBuffer[0], labelBuffer[1], labelPaintLeft);
                canvas.drawText(topLabel, labelBuffer[4], labelBuffer[5], labelPaintRight);
            }
            if (showMinLabel) {
                canvas.drawText(bottomLabel, labelBuffer[2], labelBuffer[3], labelPaintLeft);
                canvas.drawText(bottomLabel, labelBuffer[6], labelBuffer[7], labelPaintRight);
            }
        } else if ((attribute.extremumLabelPosition & PositionType.START) != 0) {
            if (showMaxLabel) {
                canvas.drawText(topLabel, labelBuffer[0], labelBuffer[1], labelPaintLeft);
            }
            if (showMinLabel) {
                canvas.drawText(bottomLabel, labelBuffer[2], labelBuffer[3], labelPaintLeft);
            }
        } else if ((attribute.extremumLabelPosition & PositionType.END) != 0) {
            if (showMaxLabel) {
                canvas.drawText(topLabel, labelBuffer[4], labelBuffer[5], labelPaintRight);
            }
            if (showMinLabel) {
                canvas.drawText(bottomLabel, labelBuffer[6], labelBuffer[7], labelPaintRight);
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    @Override
    public void onLayoutComplete() {
        super.onLayoutComplete();
        float[] drawingNonOverlapMargin = absChartModule.getDrawingNonOverlapMargin();//非重叠边距
        if ((attribute.extremumLabelPosition & PositionType.START_AND_END) != 0) {
            labelBuffer[0] = labelBuffer[2] = viewRect.left + attribute.extremumLabelMarginHorizontal;
            labelBuffer[4] = labelBuffer[6] = viewRect.right - attribute.extremumLabelMarginHorizontal;
        } else if ((attribute.extremumLabelPosition & PositionType.START) != 0) {
            labelBuffer[0] = labelBuffer[2] = viewRect.left + attribute.extremumLabelMarginHorizontal;
        } else if ((attribute.extremumLabelPosition & PositionType.END) != 0) {
            labelBuffer[4] = labelBuffer[6] = viewRect.right - attribute.extremumLabelMarginHorizontal;
        }
        if ((attribute.extremumLabelPosition & PositionType.OUTSIDE_VERTICAL) != 0) {
            labelBuffer[1] = labelBuffer[5] = viewRect.top - drawingNonOverlapMargin[1] - attribute.extremumLabelMarginVertical;
            labelBuffer[3] = labelBuffer[7] = viewRect.bottom + textHeight + drawingNonOverlapMargin[3] + attribute.extremumLabelMarginVertical;
        } else {
            labelBuffer[1] = labelBuffer[5] = viewRect.top + textHeight + attribute.extremumLabelMarginVertical;
            labelBuffer[3] = labelBuffer[7] = viewRect.bottom - attribute.extremumLabelMarginVertical;
        }
    }

    /**
     * 格式话标签文字
     */
    private String getLabelText(ValueEntry entry) {
        if (isRate) {
            if (isQuantization) {
                return render.getAdapter().rateConversion(entry, true, false);
            } else {
                return render.getAdapter().rateConversion(entry, false, false);
            }
        } else if (isQuantization) {
            return render.getAdapter().quantizationConversion(entry, true);
        } else {
            return entry.text;
        }
    }
}
