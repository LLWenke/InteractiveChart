
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.LineStyle;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.CandleRender;

/**
 * Grid轴刻度线绘制组件
 * <p>GridLineDrawing</p>
 */

public class GridLineDrawing extends AbsDrawing<CandleRender, AbsModule<AbsEntry>> {
    private static final String TAG = "GridLineDrawing";
    private BaseAttribute attribute;//配置文件
    private final Paint gridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Grid 轴刻度线画笔
    private final Path path = new Path(); //Grid 轴刻度线绘制路径
    private final float[] gridBuffer = new float[2];
    private int lastPosition, interval;//最后位置,label间隔

    @Override
    public void onInit(CandleRender render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        gridLinePaint.setStyle(Paint.Style.STROKE);
        gridLinePaint.setStrokeWidth(attribute.lineWidth);
        gridLinePaint.setColor(attribute.lineColor);

        if (attribute.gridLineStyle == LineStyle.DOTTED) {
            gridLinePaint.setPathEffect(new DashPathEffect(new float[]{10f, 5f}, 0));
        }
    }

    @Override
    public float[] onInitMargin(float viewWidth, float viewHeight) {
        margin[3] = (attribute.gridLineStyle == LineStyle.SCALE_OUTSIDE
                ? attribute.gridScaleLineLength : 0);
        return margin;
    }

    @Override
    public boolean marginOverlap() {
        return false;
    }

    @Override
    public void readyComputation(Canvas canvas, int begin, int end, float[] extremum) {
        lastPosition = render.getAdapter().getLastPosition();
        interval = (int) Math.max(1, attribute.visibleCount / attribute.gridCount);
    }

    @Override
    public void onComputation(int begin, int end, int current, float[] extremum) {
        if (attribute.gridLineStyle == LineStyle.NONE) return;
        //每隔特定个 entry，记录一个 X 轴label的位置信息和值
        if (current == 0 || current == lastPosition || current % interval != 0) return;
        gridBuffer[0] = current + 0.5f;
        render.mapPoints(absChartModule.getMatrix(), gridBuffer);
        if (attribute.gridLineStyle == LineStyle.DOTTED || attribute.gridLineStyle == LineStyle.SOLID) {
            path.moveTo(gridBuffer[0], viewRect.top);
            path.lineTo(gridBuffer[0], viewRect.bottom);
        } else if (attribute.gridLineStyle == LineStyle.SCALE_INSIDE) {
            path.moveTo(gridBuffer[0], viewRect.bottom);
            path.lineTo(gridBuffer[0], viewRect.bottom - attribute.gridScaleLineLength);
        } else if (attribute.gridLineStyle == LineStyle.SCALE_OUTSIDE) {
            path.moveTo(gridBuffer[0], viewRect.bottom);
            path.lineTo(gridBuffer[0], viewRect.bottom + attribute.gridScaleLineLength);
        }
    }

    @Override
    public void onDraw(Canvas canvas, int begin, int end, float[] extremum) {
        if (path.isEmpty()) return;
        canvas.save();
        canvas.clipRect(viewRect);
        canvas.drawPath(path, gridLinePaint);
        path.rewind();
        canvas.restore();
    }
}
