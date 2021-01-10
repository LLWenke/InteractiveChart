
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.BorderStyle;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

/**
 * 边框线绘制组件
 * <p>BorderDrawing</p>
 */

public class BorderDrawing extends AbsDrawing<AbsRender<?, ?>, AbsModule<AbsEntry>> {
    private static final String TAG = "BorderDrawing";
    private BaseAttribute attribute;//配置文件
    private final int borderStyle;
    private final Paint borderPaint = new Paint();  //边框线画笔
    private final Path borderPath = new Path();// 边框线绘制路径

    public BorderDrawing() {
        this(BorderStyle.ALL);
    }

    public BorderDrawing(int borderStyle) {
        this.borderStyle = borderStyle;
    }

    @Override
    public void onInit(AbsRender<?, ?> render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(attribute.borderWidth);
        borderPaint.setColor(attribute.borderColor);
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
        // 绘制外层边框线
        if (borderPath.isEmpty()) {
            return;
        }
        canvas.drawPath(borderPath, borderPaint);
    }

    @Override
    public void onViewChange() {
        borderPath.rewind();
        if (attribute.borderWidth <= 0f) {
            return;
        }
        float[] borderPts = render.getBorderPoints(viewRect.left, viewRect.top, viewRect.right, viewRect.bottom);
        if (borderStyle == BorderStyle.ALL || (borderStyle & BorderStyle.LEFT) != 0) {
            borderPath.moveTo(borderPts[0], borderPts[1]);
            borderPath.lineTo(borderPts[0], borderPts[3]);
        }
        if (borderStyle == BorderStyle.ALL || (borderStyle & BorderStyle.TOP) != 0) {
            borderPath.moveTo(borderPts[0], borderPts[1]);
            borderPath.lineTo(borderPts[2], borderPts[1]);
        }
        if (borderStyle == BorderStyle.ALL || (borderStyle & BorderStyle.RIGHT) != 0) {
            borderPath.moveTo(borderPts[2], borderPts[1]);
            borderPath.lineTo(borderPts[2], borderPts[3]);
        }
        if (borderStyle == BorderStyle.ALL || (borderStyle & BorderStyle.BOTTOM) != 0) {
            borderPath.moveTo(borderPts[0], borderPts[3]);
            borderPath.lineTo(borderPts[2], borderPts[3]);
        }
    }
}
