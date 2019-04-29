
package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import com.ll.chart.compat.DisplayTypeUtils;
import com.ll.chart.compat.FontConfig;
import com.ll.chart.compat.attribute.BaseAttribute;
import com.ll.chart.module.base.AbsChartModule;
import com.ll.chart.render.CandleRender;

/**
 * >Grid轴绘制组件
 * <p>GridDrawing</p>
 */

public class GridDrawing extends AbsDrawing<CandleRender> {
  private static final String TAG = "GridDrawing";
  private BaseAttribute attribute;//配置文件

  private TextPaint gridLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Grid 轴标签的画笔
  private Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Grid 轴网格线画笔

  private final float[] pointCache = new float[2];

  private float gridLabelY;//gridLabel的Y轴坐标

  @Override public void onInit(CandleRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    gridLabelPaint.setTypeface(FontConfig.typeFace);
    gridLabelPaint.setTextSize(attribute.labelSize);
    gridLabelPaint.setColor(attribute.labelColor);
    gridLabelPaint.setTextAlign(Paint.Align.CENTER);

    gridPaint.setStyle(Paint.Style.STROKE);
    gridPaint.setStrokeWidth(attribute.lineWidth);
    gridPaint.setColor(attribute.labelColor);
  }

  @Override
  public void computePoint(int begin, int end, int current) {

  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    int last = render.getAdapter().getLastPosition();
    //每隔特定个 entry，绘制一条竖向网格线和 X 轴 label
    for (int i = begin; i < end; i++) {
      if (i == 0 || i == last || i % render.getInterval() != 0) {
        continue;
      }
      pointCache[0] = i + 0.5f;
      render.mapPoints(pointCache);
      canvas.drawText(DisplayTypeUtils.format(render.getAdapter().getItem(i).getTime(),
          render.getAdapter().getDisplayType()), pointCache[0], gridLabelY, gridLabelPaint);

      // 跳过超出显示区域的线
      if (attribute.gridIsHide || pointCache[0] < viewRect.left || pointCache[0] > viewRect.right) {
        continue;
      }
      canvas.drawLines(render.getMeasureUtils().buildViewLRCoordinates
          (pointCache[0], pointCache[0]), gridPaint);
    }
  }

  @Override
  public void onDrawOver(Canvas canvas) {
  }

  @Override public void onViewChange() {
    gridLabelY = viewRect.bottom - attribute.gridLabelMarginBottom;
  }
}
