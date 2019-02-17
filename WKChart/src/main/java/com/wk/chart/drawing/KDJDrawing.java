
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.render.CandleRender;
import com.wk.chart.stock.base.AbsChartModule;

/**
 * <p>KDJDrawing</p>
 */

public class KDJDrawing extends AbsDrawing<CandleRender> {
  private CandleAttribute attribute;//配置文件

  private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //边框画笔
  private Paint centerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //中心线画笔

  private Paint kPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint dPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint jPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private float[] kBuffer = new float[4];
  private float[] dBuffer = new float[4];
  private float[] jBuffer = new float[4];

  private float[] gridBuffer = new float[2];

  @Override public void onInit(RectF viewRect, CandleRender render, AbsChartModule chartModule) {
    super.onInit(viewRect, render, chartModule);
    attribute = render.getAttribute();

    centerLinePaint.setColor(attribute.centerLineColor);
    centerLinePaint.setStrokeWidth(attribute.centerLineWidth);
    centerLinePaint.setStyle(Paint.Style.FILL);

    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeWidth(attribute.borderWidth);
    borderPaint.setColor(attribute.borderColor);

    kPaint.setStyle(Paint.Style.STROKE);
    kPaint.setStrokeWidth(attribute.normLineWidth);
    kPaint.setColor(attribute.kdjKLineColor);

    dPaint.setStyle(Paint.Style.STROKE);
    dPaint.setStrokeWidth(attribute.normLineWidth);
    dPaint.setColor(attribute.kdjDLineColor);

    jPaint.setStyle(Paint.Style.STROKE);
    jPaint.setStrokeWidth(attribute.normLineWidth);
    jPaint.setColor(attribute.kdjJLineColor);
  }

  @Override
  public void computePoint(int begin, int end, int current) {
    final int count = (end - begin) * 4;
    final int next = current + 1;
    final int i = current - begin;
    final int left = i * 4;
    final int top = i * 4 + 1;
    final int right = i * 4 + 2;
    final int bottom = i * 4 + 3;
    final CandleEntry entry = render.getAdapter().getItem(current);

    if (kBuffer.length < count) {
      kBuffer = new float[count];
      dBuffer = new float[count];
      jBuffer = new float[count];
    }

    if (current < end - 1) {
      kBuffer[left] = current + 0.5f;
      kBuffer[top] = entry.getK().value;
      kBuffer[right] = current + 1.5f;
      kBuffer[bottom] = render.getAdapter().getItem(next).getK().value;

      dBuffer[left] = kBuffer[left];
      dBuffer[top] = entry.getD().value;
      dBuffer[right] = kBuffer[right];
      dBuffer[bottom] = render.getAdapter().getItem(next).getD().value;

      jBuffer[left] = kBuffer[left];
      jBuffer[top] = entry.getJ().value;
      jBuffer[right] = kBuffer[right];
      jBuffer[bottom] = render.getAdapter().getItem(next).getJ().value;
    }
  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    canvas.save();
    canvas.clipRect(viewRect);

    gridBuffer[0] = 0;
    gridBuffer[1] = (extremum[3] + extremum[1]) / 2;
    render.mapPoints(gridBuffer);

    canvas.drawLine(viewRect.left, gridBuffer[1], viewRect.right, gridBuffer[1], centerLinePaint);

    render.mapPoints(kBuffer);
    render.mapPoints(dBuffer);
    render.mapPoints(jBuffer);

    final int count = (end - begin) * 4;
    canvas.drawLines(kBuffer, 0, count, kPaint);
    canvas.drawLines(dBuffer, 0, count, dPaint);
    canvas.drawLines(jBuffer, 0, count, jPaint);

    canvas.restore();
  }

  @Override
  public void onDrawOver(Canvas canvas) {
    if (attribute.borderWidth > 0) {
      canvas.drawRect(viewRect.left - render.getBorderCorrection(),
          viewRect.top - render.getBorderCorrection(),
          viewRect.right + render.getBorderCorrection(),
          viewRect.bottom + render.getBorderCorrection(),
          borderPaint);
    }
  }

  @Override public void onViewChange() {

  }
}
