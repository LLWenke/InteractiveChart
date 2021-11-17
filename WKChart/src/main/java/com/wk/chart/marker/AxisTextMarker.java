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
import com.wk.chart.enumeration.PositionType;
import com.wk.chart.render.AbsRender;

/**
 * <p>AxisTextMarker</p>
 */

public class AxisTextMarker extends AbsMarker<AbsRender<?, ?>> {
    private static final String TAG = "AxisTextMarker";

    private final TextPaint markerTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint markerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF markerInsets = new RectF(0, 0, 0, 0);
    private final Rect textRect = new Rect();
    private float width, height, inset, charsWidth = 0;

    @Override
    public void onInit(AbsRender<?, ?> render) {
        super.onInit(render);
        markerTextPaint.setTextAlign(Paint.Align.CENTER);
        markerTextPaint.setColor(attribute.markerTextColor);
        markerTextPaint.setTextSize(attribute.markerTextSize);

        markerBorderPaint.setStyle(attribute.markerStyle);
        markerBorderPaint.setStrokeWidth(attribute.markerBorderWidth);
        markerBorderPaint.setColor(attribute.markerBorderColor);

        //用于计算的文字宽度
        Utils.measureTextArea(markerTextPaint, textRect);
        charsWidth = textRect.width() + Utils.sp2px(attribute.context, 0.5f);
        inset = attribute.markerBorderWidth / 2;
        width = (attribute.markerPaddingHorizontal + attribute.markerBorderWidth) * 2f;
        height = textRect.height() + (attribute.markerPaddingVertical + attribute.markerBorderWidth) * 2f;
    }

    @Override
    public void onMarkerViewMeasure(RectF viewRect, Matrix matrix, float highlightPointX,
                                    float highlightPointY, String[] markerText,
                                    @Size(min = 4) @NonNull float[] markerViewInfo) {
        if (viewRect.top > highlightPointY || highlightPointY > viewRect.bottom) {
            return;
        }
        int length = markerText[1].length();
        markerTextPaint.getTextBounds(markerText[1], 0, length, textRect);
        float markerWidth = width + charsWidth * length + charsWidth / 2f;

        highlightPointY = highlightPointY - height / 2;
        if (highlightPointY < viewRect.top) {
            highlightPointY = viewRect.top + inset;
        }
        if (highlightPointY > viewRect.bottom - height) {
            highlightPointY = viewRect.bottom - height - inset;
        }

        if ((attribute.axisMarkerPosition & PositionType.START) != 0) {
            markerInsets.left = viewRect.left + inset;
        } else if ((attribute.axisMarkerPosition & PositionType.END) != 0) {
            markerInsets.left = viewRect.right - markerWidth - inset;
        } else if (highlightPointX > viewRect.left + viewRect.width() / 2) {
            markerInsets.left = viewRect.right - markerWidth - inset;
        } else {
            markerInsets.left = viewRect.left + inset;
        }

        markerInsets.top = highlightPointY;
        markerInsets.right = markerInsets.left + markerWidth;
        markerInsets.bottom = markerInsets.top + height;

        markerViewInfo[0] = markerInsets.left - inset;
        markerViewInfo[2] = markerInsets.right + inset;
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
