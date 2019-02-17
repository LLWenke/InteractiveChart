
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.wk.chart.compat.attribute.CandleAttribute;
import com.wk.chart.entry.CandleEntry;
import com.wk.chart.render.CandleRender;
import com.wk.chart.stock.base.AbsChartModule;

/**
 * <p>MADrawing</p>
 */

public class MADrawing extends AbsDrawing<CandleRender> {
  private static final String TAG = "MADrawing";
  private CandleAttribute attribute;//配置文件

  private Paint ma5Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint ma10Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private Paint ma20Paint = new Paint(Paint.ANTI_ALIAS_FLAG);

  // 计算 MA(5, 10, 20) 线条坐标用的
  private float[] ma5Buffer = new float[4];
  private float[] ma10Buffer = new float[4];
  private float[] ma20Buffer = new float[4];

  @Override public void onInit(RectF viewRect, CandleRender render, AbsChartModule chartModule) {
    super.onInit(viewRect, render, chartModule);
    attribute = render.getAttribute();

    ma5Paint.setStyle(Paint.Style.STROKE);
    ma5Paint.setStrokeWidth(attribute.normLineWidth);
    ma5Paint.setColor(attribute.ma5Color);

    ma10Paint.setStyle(Paint.Style.STROKE);
    ma10Paint.setStrokeWidth(attribute.normLineWidth);
    ma10Paint.setColor(attribute.ma10Color);

    ma20Paint.setStyle(Paint.Style.STROKE);
    ma20Paint.setStrokeWidth(attribute.normLineWidth);
    ma20Paint.setColor(attribute.ma20Color);
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

    if (ma5Buffer.length < count) {
      ma5Buffer = new float[count];
      ma10Buffer = new float[count];
      ma20Buffer = new float[count];
    }

    if (current < end - 1) {
      switch (absChartModule.getModuleType()) {
        case CANDLE:
          ma20Buffer[left] = ma10Buffer[left] = ma5Buffer[left] = current + 0.5f;
          ma20Buffer[right] = ma10Buffer[right] = ma5Buffer[right] = current + 1.5f;

          ma5Buffer[top] = entry.getMa5().value;
          ma5Buffer[bottom] = render.getAdapter().getItem(next).getMa5().value;

          ma10Buffer[top] = entry.getMa10().value;
          ma10Buffer[bottom] = render.getAdapter().getItem(next).getMa10().value;

          ma20Buffer[top] = entry.getMa20().value;
          ma20Buffer[bottom] = render.getAdapter().getItem(next).getMa20().value;
          break;

        case VOLUME:
          ma10Buffer[left] = ma5Buffer[left] = current + 0.5f;
          ma10Buffer[right] = ma5Buffer[right] = current + 1.5f;

          ma5Buffer[top] = entry.getVolumeMa5().value;
          ma5Buffer[bottom] = render.getAdapter().getItem(next).getVolumeMa5().value;

          ma10Buffer[top] = entry.getVolumeMa10().value;
          ma10Buffer[bottom] = render.getAdapter().getItem(next).getVolumeMa10().value;

          ma20Buffer = null;
          break;
      }
    }
  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    canvas.save();
    canvas.clipRect(viewRect);

    int count = (end - begin) * 4;
    count = count < ma5Buffer.length ? count : ma5Buffer.length - 1;
    // 使用 drawLines 方法比依次调用 drawLine 方法要快
    render.mapPoints(ma5Buffer);
    canvas.drawLines(ma5Buffer, 0, count, ma5Paint);

    render.mapPoints(ma10Buffer);
    canvas.drawLines(ma10Buffer, 0, count, ma10Paint);

    if (null != ma20Buffer) {
      render.mapPoints(ma20Buffer);
      canvas.drawLines(ma20Buffer, 0, count, ma20Paint);
    }

    canvas.restore();
  }

  @Override
  public void onDrawOver(Canvas canvas) {
  }

  @Override public void onViewChange() {

  }
}
