

package com.ll.chart.marker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import com.ll.chart.enumeration.AxisMarkerAlign;
import com.ll.chart.render.AbsRender;

/**
 * <p>GridTextMarker</p>
 */

public class AxisTextMarker extends AbsMarker<AbsRender> {
  private static final String TAG = "AxisTextMarker";

  private Paint markerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint markerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private final RectF markerInsets = new RectF(0, 0, 0, 0);
  private Rect textRect = new Rect();
  private AxisMarkerAlign xMarkerAlign;
  private float inset = 0;
  private float width = 0;
  private float height = 0;

  @Override
  public void onInit(AbsRender render) {
    super.onInit(render);
    markerTextPaint.setTextAlign(Paint.Align.CENTER);
    markerTextPaint.setColor(attribute.markerTextColor);
    markerTextPaint.setTextSize(attribute.markerTextSize);

    markerBorderPaint.setStyle(attribute.markerStyle);
    markerBorderPaint.setStrokeWidth(attribute.markerBorderWidth);
    markerBorderPaint.setColor(attribute.markerBorderColor);
    inset = attribute.markerBorderWidth / 2;

    xMarkerAlign = attribute.xMarkerAlign;
  }

  @Override public void onMarkerViewMeasure(RectF viewRect,
      Matrix matrix, float highlightPointX,
      float highlightPointY, String[] markerText, @Size(min = 4) @NonNull float[] markerViewInfo) {
    if (viewRect.left > highlightPointX || highlightPointX > viewRect.right) {
      return;
    }
    markerTextPaint.getTextBounds(markerText[0], 0,
        markerText[0].length(), textRect);
    width = Math.max(width,
        textRect.width() + (attribute.markerBorderLRPadding - attribute.markerBorderWidth) * 2);
    height = Math.max(height,
        textRect.height() + (attribute.markerBorderTBPadding - attribute.markerBorderWidth) * 2);

    highlightPointX = highlightPointX - width / 2;
    if (highlightPointX <= viewRect.left) {
      highlightPointX = viewRect.left + inset;
    }
    if (highlightPointX >= viewRect.right - width) {
      highlightPointX = viewRect.right - width - inset;
    }

    markerInsets.left = highlightPointX;

    if (xMarkerAlign == AxisMarkerAlign.TOP) {
      markerInsets.top = viewRect.top + inset;
    } else if (xMarkerAlign == AxisMarkerAlign.BOTTOM) {
      markerInsets.top = viewRect.bottom - height - inset;
    } else if (highlightPointY < viewRect.top + viewRect.height() / 2) {
      markerInsets.top = viewRect.bottom - height - inset;
    } else {
      markerInsets.top = viewRect.top + inset;
    }

    markerInsets.right = markerInsets.left + width;
    markerInsets.bottom = markerInsets.top + height;

    markerViewInfo[1] = markerInsets.top - inset;
    markerViewInfo[3] = markerInsets.bottom + inset;
  }

  @Override public void onMarkerViewDraw(Canvas canvas, String[] markerText) {
    canvas.drawRoundRect(markerInsets, attribute.markerBorderRadius, attribute.markerBorderRadius,
        markerBorderPaint);

    canvas.drawText(markerText[0],
        markerInsets.left + markerInsets.width() / 2,
        markerInsets.top + (markerInsets.height() + textRect.height()) / 2,
        markerTextPaint);
  }
}
