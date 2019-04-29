
package com.ll.chart.drawing.depth;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import com.ll.chart.compat.FontConfig;
import com.ll.chart.compat.Utils;
import com.ll.chart.compat.ValueUtils;
import com.ll.chart.compat.attribute.BaseAttribute;
import com.ll.chart.drawing.AbsDrawing;
import com.ll.chart.module.base.AbsChartModule;
import com.ll.chart.render.DepthRender;

/**
 * >Grid轴绘制组件
 * <p>DepthGridDrawing</p>
 */

public class DepthGridDrawing extends AbsDrawing<DepthRender> {
  private static final String TAG = "GridDrawing";
  private BaseAttribute attribute;//配置文件

  private TextPaint gridLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Grid 轴标签的画笔
  private Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Grid 轴网格线画笔

  private Rect rect = new Rect();//用于测量文字的实际占用区域

  private final float[] pointCache = new float[2];

  private float regionWidth, gridLabelY;//区域宽度,gridLabel的 Grid 轴坐标

  @Override public void onInit(DepthRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    gridLabelPaint.setTypeface(FontConfig.typeFace);
    gridLabelPaint.setTextSize(attribute.labelSize);
    gridLabelPaint.setColor(attribute.labelColor);
    gridLabelPaint.setTextAlign(Paint.Align.CENTER);

    gridPaint.setStyle(Paint.Style.STROKE);
    gridPaint.setStrokeWidth(attribute.lineWidth);
    gridPaint.setColor(attribute.labelColor);

    Utils.measureTextArea(gridLabelPaint, rect);
  }

  @Override
  public void computePoint(int begin, int end, int current) {

  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    for (int i = 0, z = attribute.gridCount - 1; i <= z; i++) {
      float x = viewRect.left + i * regionWidth;
      String value;
      if (i == 0) {
        value = absChartModule.getMinX().text;
        pointCache[0] = x + gridLabelPaint.measureText(value) / 2 + attribute.gridMarkLineLength;
      } else if (i == z) {
        value = absChartModule.getMaxX().text;
        pointCache[0] = x - gridLabelPaint.measureText(value) / 2 - attribute.gridMarkLineLength;
      } else {
        pointCache[0] = x - (i > (z / 2) ? absChartModule.getxOffset() :
            -absChartModule.getxOffset());
        render.invertMapPoints(pointCache);
        value = ValueUtils.format(pointCache[0], render.getAdapter().getQuoteScale());
        pointCache[0] = x;
      }
      canvas.drawText(value, pointCache[0], gridLabelY, gridLabelPaint);
    }
  }

  @Override
  public void onDrawOver(Canvas canvas) {
  }

  @Override public void onViewChange() {
    regionWidth = viewRect.width() / (attribute.gridCount - 1);
    gridLabelY = viewRect.bottom + rect.height() + attribute.gridLabelMarginTop;
  }
}
