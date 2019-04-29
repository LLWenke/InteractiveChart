
package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.ll.chart.compat.attribute.BaseAttribute;
import com.ll.chart.render.AbsRender;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>EmptyDataDrawing</p>
 */

public class EmptyDataDrawing extends AbsDrawing<AbsRender> {
  private BaseAttribute attribute;//配置文件

  private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final Paint.FontMetrics fontMetrics = new Paint.FontMetrics();

  @Override public void onInit(AbsRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    textPaint.setTextAlign(Paint.Align.CENTER);
  }

  @Override
  public void computePoint(int begin, int end, int current) {

  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {

  }

  @Override
  public void onDrawOver(Canvas canvas) {
    if (render.getAdapter().getCount() == 0) {
      final String drawText;
      textPaint.setTextSize(attribute.loadingTextSize);
      textPaint.setColor(attribute.loadingTextColor);
      drawText = attribute.loadingText;

      textPaint.getFontMetrics(fontMetrics);

      canvas.drawText(drawText,
          viewRect.width() / 2,
          (viewRect.top + viewRect.bottom - fontMetrics.top - fontMetrics.bottom) / 2,
          textPaint);
    }
  }

  @Override public void onViewChange() {

  }
}
