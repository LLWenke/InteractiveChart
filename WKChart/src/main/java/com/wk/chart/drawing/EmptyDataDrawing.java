
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

/**
 * <p>EmptyDataDrawing</p>
 */

public class EmptyDataDrawing extends AbsDrawing<AbsRender<?, ?>, AbsModule<AbsEntry>> {
    private BaseAttribute attribute;//配置文件

    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

    @Override
    public void onInit(AbsRender<?, ?> render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {

    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {

    }

    @Override
    public void drawOver(Canvas canvas) {
        if (render.getAdapter().getCount() == 0) {
            final String drawText;
            textPaint.setTextSize(attribute.loadingTextSize);
            textPaint.setColor(attribute.loadingTextColor);
            drawText = attribute.loadingText;

            textPaint.getFontMetrics(fontMetrics);

            canvas.drawText(drawText,
                    viewRect.width() / 2,
                    (viewRect.top + viewRect.bottom - fontMetrics.top - fontMetrics.bottom) / 2,
                    textPaint);
        }
    }

    @Override
    public void onViewChange() {

    }
}
