
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.render.CandleRender;
import com.wk.chart.stock.base.AbsChartModule;

/**
 * <p>BOLLDrawing</p>
 */

public class BOLLDrawing extends AbsDrawing<CandleRender> {
  private CandleAttribute attribute;//配置文件

  private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //边框画笔
  private Paint centerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //中心线画笔

  private Paint r1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint r2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint r3Paint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private float[] r1Buffer = new float[4];
  private float[] r2Buffer = new float[4];
  private float[] r3Buffer = new float[4];

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

    r1Paint.setColor(attribute.bollMidLineColor);
    r1Paint.setStrokeWidth(attribute.normLineWidth);
    r1Paint.setStyle(Paint.Style.STROKE);

    r2Paint.setColor(attribute.bollUpperLineColor);
    r2Paint.setStrokeWidth(attribute.normLineWidth);
    r2Paint.setStyle(Paint.Style.STROKE);

    r3Paint.setColor(attribute.bollLowerLineColor);
    r3Paint.setStrokeWidth(attribute.normLineWidth);
    r3Paint.setStyle(Paint.Style.STROKE);
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

    if (r1Buffer.length < count) {
      r1Buffer = new float[count];
      r2Buffer = new float[count];
      r3Buffer = new float[count];
    }

    if (current < end - 1) {
      r1Buffer[left] = current + 0.5f;
      r1Buffer[top] = entry.getMb().value;
      r1Buffer[right] = current + 1.5f;
      r1Buffer[bottom] = render.getAdapter().getItem(next).getMb().value;

      r2Buffer[left] = r1Buffer[left];
      r2Buffer[top] = entry.getUp().value;
      r2Buffer[right] = r1Buffer[right];
      r2Buffer[bottom] = render.getAdapter().getItem(next).getUp().value;

      r3Buffer[left] = r1Buffer[left];
      r3Buffer[top] = entry.getDn().value;
      r3Buffer[right] = r1Buffer[right];
      r3Buffer[bottom] = render.getAdapter().getItem(next).getDn().value;
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

    render.mapPoints(r1Buffer);
    render.mapPoints(r2Buffer);
    render.mapPoints(r3Buffer);

    final int count = (end - begin) * 4;
    final int offset = begin == 0 ? 4 : 0;
    canvas.drawLines(r1Buffer, 0, count, r1Paint);
    canvas.drawLines(r2Buffer, offset, count - offset, r2Paint);
    canvas.drawLines(r3Buffer, offset, count - offset, r3Paint);

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
