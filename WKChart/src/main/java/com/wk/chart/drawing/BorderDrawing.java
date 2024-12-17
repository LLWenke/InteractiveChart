
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.drawing.base.AbsDrawing;
import com.wk.chart.entry.AbsEntry;
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.AbsRender;

/**
 * <p>边框组件</p>
 */

public class BorderDrawing extends AbsDrawing<AbsRender<?, ?>, AbsModule<AbsEntry>> {
    private static final String TAG = "BorderDrawing";
    private BaseAttribute attribute;//配置文件
    private final int borderPosition;//边框位置
    private final Paint borderPaint = new Paint();//边框线画笔
    private final Path borderPath = new Path();//边框线绘制路径
    private final float[] borderPts = new float[4];// 边框线位置信息
    private float borderOffset;//边框偏移量

    public BorderDrawing() {
        this(PositionType.ALL);
    }

    public BorderDrawing(int positionType) {
        this.borderPosition = positionType;
    }

    @Override
    public void onInit(AbsRender<?, ?> render, AbsModule<AbsEntry> chartModule) {
        super.onInit(render, chartModule);
        attribute = render.getAttribute();

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(attribute.borderWidth);
        borderPaint.setColor(attribute.borderColor);

        borderOffset = attribute.borderWidth / 2f;
    }

    @Override
    public float[] onInitMargin(float viewWidth, float viewHeight) {
        if (borderPosition == PositionType.ALL || (borderPosition & PositionType.START) != 0) {
            margin[0] = attribute.borderWidth;
        }
        if (borderPosition == PositionType.ALL || (borderPosition & PositionType.TOP) != 0) {
            margin[1] = attribute.borderWidth;
        }
        if (borderPosition == PositionType.ALL || (borderPosition & PositionType.END) != 0) {
            margin[2] = attribute.borderWidth;
        }
        if (borderPosition == PositionType.ALL || (borderPosition & PositionType.BOTTOM) != 0) {
            margin[3] = attribute.borderWidth;
        }
        return margin;
    }

    @Override
    public boolean marginOverlap() {
        return false;
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
    public void onLayoutComplete() {
        super.onLayoutComplete();
        borderPath.rewind();
        if (attribute.borderWidth <= 0f) return;
        borderPts[0] = viewRect.left - borderOffset;
        borderPts[1] = viewRect.top - borderOffset;
        borderPts[2] = viewRect.right + borderOffset;
        borderPts[3] = viewRect.bottom + borderOffset;
        if (borderPosition == PositionType.ALL || (borderPosition & PositionType.START) != 0) {
            borderPath.moveTo(borderPts[0], borderPts[1]);
            borderPath.lineTo(borderPts[0], borderPts[3]);
        }
        if (borderPosition == PositionType.ALL || (borderPosition & PositionType.TOP) != 0) {
            borderPath.moveTo(borderPts[0], borderPts[1]);
            borderPath.lineTo(borderPts[2], borderPts[1]);
        }
        if (borderPosition == PositionType.ALL || (borderPosition & PositionType.END) != 0) {
            borderPath.moveTo(borderPts[2], borderPts[1]);
            borderPath.lineTo(borderPts[2], borderPts[3]);
        }
        if (borderPosition == PositionType.ALL || (borderPosition & PositionType.BOTTOM) != 0) {
            borderPath.moveTo(borderPts[0], borderPts[3]);
            borderPath.lineTo(borderPts[2], borderPts[3]);
        }
    }
}
