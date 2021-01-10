package com.wk.chart.marker;


import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

import androidx.annotation.NonNull;
import androidx.annotation.Size;

import com.wk.chart.compat.Utils;
import com.wk.chart.enumeration.GridMarkerAlign;
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
    private float width, height, inset = 0;

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

        inset = attribute.markerBorderWidth / 2;
        width = (attribute.markerLRPadding + attribute.markerBorderWidth) * 2f;
        height = textRect.height() + (attribute.markerTBPadding + attribute.markerBorderWidth) * 2f;

        switch (attribute.gridMarkerAlign) {
            case TOP://上
                setMargin(0, height, 0, 0);
                break;
            case BOTTOM://下
                setMargin(0, 0, 0, height);
                break;
        }
    }

    @Override
    public void onMarkerViewMeasure(RectF viewRect, Matrix matrix, float highlightPointX,
                                    float highlightPointY, String[] markerText,
                                    @Size(min = 4) @NonNull float[] markerViewInfo) {
        if (viewRect.left > highlightPointX || highlightPointX > viewRect.right) {
            return;
        }
        markerTextPaint.getTextBounds(markerText[0], 0, markerText[0].length(), textRect);
        float markerWidth = width + textRect.width();

        highlightPointX = highlightPointX - markerWidth / 2;
        if (highlightPointX <= viewRect.left) {
            highlightPointX = viewRect.left + inset;
        }
        if (highlightPointX >= viewRect.right - markerWidth) {
            highlightPointX = viewRect.right - markerWidth - inset;
        }

        markerInsets.left = highlightPointX;

        if (attribute.gridMarkerAlign == GridMarkerAlign.TOP_INSIDE) {
            markerInsets.top = render.getTopModule().getRect().top + inset;
        } else if (attribute.gridMarkerAlign == GridMarkerAlign.TOP) {
            markerInsets.top = render.getTopModule().getRect().top - height - attribute.borderWidth;
        } else if (attribute.gridMarkerAlign == GridMarkerAlign.BOTTOM_INSIDE) {
            markerInsets.top = render.getBottomModule().getRect().bottom - height - inset;
        } else if (attribute.gridMarkerAlign == GridMarkerAlign.BOTTOM) {
            markerInsets.top = render.getBottomModule().getRect().bottom + attribute.borderWidth;
        } else if (highlightPointY < viewRect.top + viewRect.height() / 2) {
            markerInsets.top = viewRect.bottom - height - inset;
        } else {
            markerInsets.top = viewRect.top + inset;
        }

        markerInsets.right = markerInsets.left + markerWidth;
        markerInsets.bottom = markerInsets.top + height;

        markerViewInfo[1] = markerInsets.top - inset;
        markerViewInfo[3] = markerInsets.bottom + inset;
    }

    @Override
    public void onMarkerViewDraw(Canvas canvas, String[] markerText) {
        canvas.drawRoundRect(markerInsets, attribute.markerRadius, attribute.markerRadius,
                markerBorderPaint);

        canvas.drawText(markerText[0],
                markerInsets.left + markerInsets.width() / 2,
                markerInsets.top + (markerInsets.height() + textRect.height()) / 2,
                markerTextPaint);
    }
}
