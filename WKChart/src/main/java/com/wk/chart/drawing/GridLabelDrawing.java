
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;

import com.wk.chart.compat.FontStyle;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.CandleRender;

/**
 * Grid轴标签绘制组件
 * <p>GridLabelDrawing</p>
 */

public class GridLabelDrawing extends AbsDrawing<CandleRender, AbsModule<AbsEntry>> {
    private static final String TAG = "GridLabelDrawing";
    private BaseAttribute attribute;//配置文件
    private final TextPaint gridLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Grid 轴标签的画笔
    private final float[] gridBuffer = new float[2];
    private final Rect rect = new Rect(); //用于测量文字的实际占用区域
    private float gridLabelY;//gridLabel的Y轴坐标
    private int lastPosition, interval;//最后位置,label间隔

    @Override
    public void onInit(CandleRender render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        gridLabelPaint.setTypeface(FontStyle.typeFace);
        gridLabelPaint.setTextSize(attribute.labelSize);
        gridLabelPaint.setColor(attribute.labelColor);
        gridLabelPaint.setTextAlign(Paint.Align.CENTER);

        Utils.measureTextArea(gridLabelPaint, rect);
    }

    @Override
    public float[] onInitMargin(float viewWidth, float viewHeight) {
        margin[3] = (float) Math.ceil(attribute.gridLabelMarginVertical * 2f + rect.height());
        return margin;
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {
        lastPosition = render.getAdapter().getLastPosition();
        interval = (int) Math.max(1, attribute.visibleCount / attribute.gridCount);
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        for (int i = begin; i < end; i++) {
            //每隔特定个 entry，记录一个 X 轴label的位置信息和值
            if (i == 0 || i == lastPosition || i % interval != 0) continue;
            gridBuffer[0] = i + 0.5f;
            render.mapPoints(render.getMainModule().getMatrix(), gridBuffer);
            String label = render.getAdapter().getItem(i).getShortTimeText();
            canvas.drawText(label, gridBuffer[0], gridLabelY, gridLabelPaint);
        }
    }

    @Override
    public void onLayoutComplete() {
        super.onLayoutComplete();
        float[] drawingNonOverlapMargin = chartModule.getDrawingNonOverlapMargin();//非重叠边距
        gridLabelY = viewRect.bottom
                + drawingNonOverlapMargin[3]
                + attribute.gridLabelMarginVertical
                + rect.height();
    }
}
