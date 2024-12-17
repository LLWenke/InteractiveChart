package com.wk.chart.drawing.child;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

import com.wk.chart.compat.Utils;
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.module.AbsModule;
import com.wk.chart.render.AbsRender;

/**
 * <p>AxisTextMarker</p>
 */

public class AxisTextMarker extends AbsChildDrawing<AbsRender<?, ?>, AbsModule<?>> {
    private static final String TAG = "AxisTextMarker";
    private final TextPaint markerTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint markerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[] markerBuffer = new float[4];//标签位置信息
    private final Rect textRect = new Rect();
    private float marketPaddingHorizontal, markerHeight, textHeight, borderOffset;

    @Override
    public void onInit(AbsRender<?, ?> render, AbsModule<?> absModule) {
        super.onInit(render, absModule);
        markerTextPaint.setTextAlign(Paint.Align.CENTER);
        markerTextPaint.setColor(attribute.markerTextColor);
        markerTextPaint.setTextSize(attribute.markerTextSize);

        markerBorderPaint.setStyle(attribute.markerStyle);
        markerBorderPaint.setStrokeWidth(attribute.markerBorderWidth);
        markerBorderPaint.setColor(attribute.markerBorderColor);

        Utils.measureTextArea(markerTextPaint, textRect);
        textHeight = textRect.height();
        borderOffset = attribute.markerBorderWidth / 2f;
        marketPaddingHorizontal = (attribute.markerPaddingHorizontal + attribute.markerBorderWidth) * 2f;
        markerHeight = textHeight + (attribute.markerPaddingVertical + attribute.markerBorderWidth) * 2f;
    }

    @Override
    public float[] onMeasureChildView(RectF viewRect, float[] nonOverlapMargin, float x, float y,
                                      String markerText, boolean isReverse) {
        if (viewRect.top > y || y > viewRect.bottom) {
            return markerBuffer;
        }
        float markerWidth = marketPaddingHorizontal + markerTextPaint.measureText(markerText);
        markerBuffer[1] = y - markerHeight / 2f;
        if (markerBuffer[1] < viewRect.top) {
            markerBuffer[1] = viewRect.top;
        } else if (markerBuffer[1] > viewRect.bottom - markerHeight) {
            markerBuffer[1] = viewRect.bottom - markerHeight;
        }
        if ((attribute.axisMarkerPosition & PositionType.START) != 0) {
            markerBuffer[0] = viewRect.left;
        } else if ((attribute.axisMarkerPosition & PositionType.END) != 0) {
            markerBuffer[0] = viewRect.right - markerWidth;
        } else if (x < viewRect.left + viewRect.width() / 2f) {
            markerBuffer[0] = isReverse ? viewRect.right - markerWidth : viewRect.left;
        } else {
            markerBuffer[0] = isReverse ? viewRect.left : viewRect.right - markerWidth;
        }
        markerBuffer[2] = markerBuffer[0] + markerWidth;
        markerBuffer[3] = markerBuffer[1] + markerHeight;
        return markerBuffer;
    }

    @Override
    public void onChildViewDraw(Canvas canvas, String markerText) {
        float left = markerBuffer[0] + borderOffset;
        float top = markerBuffer[1] + borderOffset;
        float right = markerBuffer[2] - borderOffset;
        float bottom = markerBuffer[3] - borderOffset;
        float textY = top + textHeight + (bottom - top - textHeight) / 2f;
        canvas.drawRoundRect(left, top, right, bottom, attribute.markerRadius, attribute.markerRadius, markerBorderPaint);
        canvas.drawText(markerText, left + (right - left) / 2f, textY, markerTextPaint);
    }
}
