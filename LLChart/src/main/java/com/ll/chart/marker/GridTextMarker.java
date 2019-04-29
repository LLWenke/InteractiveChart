

package com.ll.chart.marker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Size;
import com.ll.chart.enumeration.GridMarkerAlign;
import com.ll.chart.render.AbsRender;

/**
 * <p>GridTextMarker</p>
 */

public class GridTextMarker extends AbsMarker<AbsRender> {
  private static final String TAG = "GridTextMarker";

  private Paint markerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint markerBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private final RectF markerInsets = new RectF(0, 0, 0, 0);
  private final Rect textRect = new Rect();
  private GridMarkerAlign yMarkerAlign;
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

    yMarkerAlign = attribute.yMarkerAlign;
  }

  @Override public void onMarkerViewMeasure(RectF viewRect, Matrix matrix, float highlightPointX,
      float highlightPointY, String[] markerText, @Size(min = 4) @NonNull float[] markerViewInfo) {
    if (viewRect.top > highlightPointY || highlightPointY > viewRect.bottom) {
      return;
    }
    markerTextPaint.getTextBounds(markerText[1], 0, markerText[1].length(), textRect);
    width = Math.max(width,
        textRect.width() + (attribute.markerBorderLRPadding - attribute.markerBorderWidth) * 2);
    height = Math.max(height,
        textRect.height() + (attribute.markerBorderTBPadding - attribute.markerBorderWidth) * 2);
    highlightPointY = highlightPointY - height / 2;
    if (highlightPointY < viewRect.top) {
      highlightPointY = viewRect.top + inset;
    }
    if (highlightPointY > viewRect.bottom - height) {
      highlightPointY = viewRect.bottom - height - inset;
    }

    if (yMarkerAlign == GridMarkerAlign.LEFT) {
      markerInsets.left = viewRect.left + inset;
    } else if (yMarkerAlign == GridMarkerAlign.RIGHT) {
      markerInsets.left = viewRect.right - width - inset;
    } else if (highlightPointX > viewRect.left + viewRect.width() / 2) {
      markerInsets.left = viewRect.right - width - inset;
    } else {
      markerInsets.left = viewRect.left + inset;
    }

    markerInsets.top = highlightPointY;
    markerInsets.right = markerInsets.left + width;
    markerInsets.bottom = markerInsets.top + height;

    markerViewInfo[0] = markerInsets.left - inset;
    markerViewInfo[2] = markerInsets.right + inset;
  }

  @Override public void onMarkerViewDraw(Canvas canvas, String[] markerText) {
    canvas.drawRoundRect(markerInsets, attribute.markerBorderRadius, attribute.markerBorderRadius,
        markerBorderPaint);

    canvas.drawText(markerText[1],
        markerInsets.left + markerInsets.width() / 2,
        markerInsets.top + (markerInsets.height() + textRect.height()) / 2,
        markerTextPaint);
  }
}
