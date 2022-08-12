package com.wk.chart.drawing.child;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Log;

import com.wk.chart.compat.Utils;
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.module.base.AbsModule;
import com.wk.chart.render.AbsRender;

/**
 * <p>GridTextMarker</p>
 */

public class GridTextMarker extends AbsChildDrawing<AbsRender<?, ?>, AbsModule<?>> {
    private static final String TAG = "GridTextMarker";
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
    public float[] onInitMargin(float viewWidth, float viewHeight) {
        float marketMargin = 0;
        if ((attribute.gridMarkerPosition & PositionType.OUTSIDE_VERTICAL) != 0) {
            marketMargin = markerHeight;
        }
        if ((attribute.gridMarkerPosition & PositionType.TOP) != 0) {
            setMargin(0, marketMargin, 0, 0);
        } else if ((attribute.gridMarkerPosition & PositionType.BOTTOM) != 0) {
            setMargin(0, 0, 0, marketMargin);
        } else {
            setMargin(0, marketMargin, 0, marketMargin);
        }
        Log.e("margin", TAG + String.valueOf(margin[3]));
        return margin;
    }

    @Override
    public float[] onMeasureChildView(RectF viewRect, float[] nonOverlapMargin, float x, float y,
                                      String markerText, boolean isReverse) {
        if (viewRect.left > x || x > viewRect.right) {
            return markerBuffer;
        }
        float markerWidth = marketPaddingHorizontal + markerTextPaint.measureText(markerText);
        float top = viewRect.top;
        float bottom = viewRect.bottom;
        markerBuffer[0] = x - markerWidth / 2f;
        if (markerBuffer[0] < viewRect.left) {
            markerBuffer[0] = viewRect.left;
        } else if (markerBuffer[0] > viewRect.right - markerWidth) {
            markerBuffer[0] = viewRect.right - markerWidth;
        }
        float topOffset, bottomOffset;
        if ((attribute.gridMarkerPosition & PositionType.OUTSIDE_VERTICAL) != 0) {
            topOffset = nonOverlapMargin[1] + markerHeight;
            bottomOffset = nonOverlapMargin[3];
        } else {
            topOffset = 0;
            bottomOffset = -markerHeight;
        }
        if ((attribute.gridMarkerPosition & PositionType.TOP) != 0) {
            markerBuffer[1] = top - topOffset;
        } else if ((attribute.gridMarkerPosition & PositionType.BOTTOM) != 0) {
            markerBuffer[1] = bottom + bottomOffset;
        } else if (y < top + (bottom - top) / 2f) {
            markerBuffer[1] = isReverse ? bottom + bottomOffset : top - topOffset;
        } else {
            markerBuffer[1] = isReverse ? top - topOffset : bottom + bottomOffset;
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
