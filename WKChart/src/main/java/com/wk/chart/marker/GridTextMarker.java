package com.wk.chart.marker;


import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.wk.chart.compat.Utils;
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.render.AbsRender;

/**
 * <p>AxisTextMarker</p>
 */

public class GridTextMarker extends AbsMarker<AbsRender<?, ?>> {
    private static final String TAG = "GridTextMarker";

    private final TextPaint markerTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint markerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF markerInsets = new RectF(0, 0, 0, 0);
    private final Rect textRect = new Rect();
    private float width, height, borderOffset = 0;

    @Override
    public void onInit(AbsRender<?, ?> render) {
        super.onInit(render);
        markerTextPaint.setTextAlign(Paint.Align.CENTER);
        markerTextPaint.setColor(attribute.markerTextColor);
        markerTextPaint.setTextSize(attribute.markerTextSize);

        markerBorderPaint.setStyle(attribute.markerStyle);
        markerBorderPaint.setStrokeWidth(attribute.markerBorderWidth);
        markerBorderPaint.setColor(attribute.markerBorderColor);

        Utils.measureTextArea(markerTextPaint, textRect);

        borderOffset = attribute.markerBorderWidth / 2;
        width = (attribute.markerPaddingHorizontal + attribute.markerBorderWidth) * 2f;
        height = textRect.height() + (attribute.markerPaddingVertical + attribute.markerBorderWidth) * 2f;
    }

    @Override
    public float[] onInitMargin() {
        float marketHeight = 0;
        if ((attribute.gridMarkerPosition & PositionType.OUTSIDE_VERTICAL) != 0) {
            marketHeight = height;
        }
        if ((attribute.gridMarkerPosition & PositionType.TOP) != 0) {
            setMargin(0, marketHeight, 0, 0);
        } else if ((attribute.gridMarkerPosition & PositionType.BOTTOM) != 0) {
            setMargin(0, 0, 0, marketHeight);
        } else {
            setMargin(0, marketHeight, 0, marketHeight);
        }
        return margin;
    }

    @Override
    public void onMarkerViewMeasure(RectF viewRect, Matrix matrix, float highlightPointX,
                                    float highlightPointY, String[] markerText,
                                    @Size(min = 4) @NonNull float[] markerViewInfo,
                                    boolean isReverse) {
        if (viewRect.left > highlightPointX || highlightPointX > viewRect.right) {
            return;
        }
        Utils.measureTextArea(markerTextPaint, textRect, markerText[1]);
        float markerWidth = width + textRect.width();
        float top = render.getTopModule().getRect().top;
        float bottom = render.getBottomModule().getRect().bottom;
        highlightPointX = highlightPointX - markerWidth / 2;
        if (highlightPointX <= viewRect.left) {
            highlightPointX = viewRect.left + borderOffset;
        }
        if (highlightPointX >= viewRect.right - markerWidth) {
            highlightPointX = viewRect.right - markerWidth - borderOffset;
        }
        markerInsets.left = highlightPointX;
        float offset;
        if ((attribute.gridMarkerPosition & PositionType.OUTSIDE_VERTICAL) != 0) {
            offset = -(height + attribute.borderWidth + borderOffset);
        } else {
            offset = borderOffset;
        }
        if ((attribute.gridMarkerPosition & PositionType.TOP) != 0) {
            markerInsets.top = top + offset;
        } else if ((attribute.gridMarkerPosition & PositionType.BOTTOM) != 0) {
            markerInsets.top = bottom - height - offset;
        } else if (highlightPointY < top + (bottom - top) / 2) {
            markerInsets.top = isReverse ? bottom - height - offset : top + offset;
        } else {
            markerInsets.top = isReverse ? top + offset : bottom - height - offset;
        }
        markerInsets.right = markerInsets.left + markerWidth;
        markerInsets.bottom = markerInsets.top + height;
        markerViewInfo[1] = markerInsets.top - borderOffset;
        markerViewInfo[3] = markerInsets.bottom + borderOffset;
    }

    @Override
    public void onMarkerViewDraw(Canvas canvas, String[] markerText) {
        canvas.drawRoundRect(markerInsets, attribute.markerRadius, attribute.markerRadius,
                markerBorderPaint);
        canvas.drawText(markerText[1],
                markerInsets.left + markerInsets.width() / 2,
                markerInsets.top + (markerInsets.height() + textRect.height()) / 2,
                markerTextPaint);
    }
}
