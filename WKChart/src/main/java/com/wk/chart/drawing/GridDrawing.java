
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import com.wk.chart.compat.DisplayTypeUtils;
import com.wk.chart.compat.FontConfig;
import com.wk.chart.compat.ValueUtils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.enumeration.ModuleType;
import com.wk.chart.render.AbsRender;
import com.wk.chart.render.CandleRender;
import com.wk.chart.render.DepthRender;
import com.wk.chart.stock.base.AbsChartModule;

/**
 * >X轴绘制组件
 * <p>GridDrawing</p>
 */

public class GridDrawing extends AbsDrawing<AbsRender> {
  private static final String TAG = "GridDrawing";
  private BaseAttribute attribute;//配置文件

  private TextPaint gridLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG); // X 轴标签的画笔
  private Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // X 轴网格线画笔

  private final float[] pointCache = new float[2];

  private float regionWidth, revised;//区域宽度,xLabel的Y轴修正值

  @Override public void onInit(RectF viewRect, AbsRender render, AbsChartModule chartModule) {
    super.onInit(viewRect, render, chartModule);
    attribute = render.getAttribute();

    gridLabelPaint.setTypeface(FontConfig.typeFace);
    gridLabelPaint.setTextSize(attribute.axisLabelSize);
    gridLabelPaint.setColor(attribute.axisLabelColor);
    gridLabelPaint.setTextAlign(Paint.Align.CENTER);

    gridPaint.setStyle(Paint.Style.STROKE);
    gridPaint.setStrokeWidth(attribute.gridWidth);
    gridPaint.setColor(attribute.gridColor);

    revised = attribute.axisLabelMarginBottom;
  }

  @Override
  public void computePoint(int begin, int end, int current) {

  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    if (render instanceof CandleRender) {
      int last = render.getAdapter().getLastPosition();
      //每隔特定个 entry，绘制一条竖向网格线和 X 轴 label
      for (int i = begin; i < end; i++) {
        if (i == 0 || i == last || i % render.getInterval() != 0) {
          continue;
        }
        pointCache[0] = i + 0.5f;
        render.mapPoints(pointCache);
        canvas.drawText(DisplayTypeUtils.format(render.getAdapter().getItem(i).getTime(),
            ((CandleRender) render).getAdapter().getDisplayType()), pointCache[0],
            viewRect.bottom - revised, gridLabelPaint);

        // 跳过超出显示区域的线
        if (attribute.gridIsHide || pointCache[0] < viewRect.left
            || pointCache[0] > viewRect.right) {
          continue;
        }
        canvas.drawLines(render.getMeasureUtils().buildViewLRCoordinates
            (pointCache[0], pointCache[0]), gridPaint);
      }
    } else if (render instanceof DepthRender) {
      for (int i = 0, z = attribute.gridCount - 1; i <= z; i++) {
        DepthRender depthRender = (DepthRender) render;
        float left = viewRect.left + i * regionWidth;
        pointCache[0] = left;
        render.invertMapPoints(depthRender.getChartModule(ModuleType.DEPTH).getMatrix(), pointCache);
        String value = ValueUtils.format(pointCache[0], depthRender.getAdapter().getQuoteScale());
        float offset = gridLabelPaint.measureText(value) / 2;
        if (i == 0) {
          pointCache[0] = left + offset + attribute.gridMarkLineLength;
        } else if (i == z) {
          pointCache[0] = left - offset - attribute.gridMarkLineLength;
        } else {
          pointCache[0] = left;
        }
        canvas.drawText(value, pointCache[0], viewRect.bottom - revised, gridLabelPaint);
      }
    }
  }

  @Override
  public void onDrawOver(Canvas canvas) {
  }

  @Override public void onViewChange() {
    regionWidth = viewRect.width() / (attribute.gridCount - 1);
  }
}
