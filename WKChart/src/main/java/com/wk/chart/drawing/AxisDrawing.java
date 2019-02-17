
package com.wk.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import com.wk.chart.compat.FontConfig;
import com.wk.chart.compat.Utils;
import com.wk.chart.compat.ValueUtils;
import com.wk.chart.compat.attribute.BaseAttribute;
import com.wk.chart.enumeration.GridLabelLocation;
import com.wk.chart.render.AbsRender;
import com.wk.chart.stock.base.AbsChartModule;

/**
 * Y轴绘制组件
 * <p>AxisDrawing</p>
 */

public class AxisDrawing extends AbsDrawing<AbsRender> {
  private static final String TAG = "AxisDrawing";
  private BaseAttribute attribute;

  private TextPaint axisLabelPaintLeft = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Y 轴标签文字的画笔(左)
  private TextPaint axisLabelPaintRight = new TextPaint(Paint.ANTI_ALIAS_FLAG); // Y 轴标签文字的画笔（右）
  private Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // Y 轴的画笔
  private Rect rect = new Rect();//用于测量文字的实际占用区域

  private final float[] pointCache = new float[2];
  private float[] lineBuffer = new float[8];

  private float left, right, textCenter, regionHeight;

  @Override public void onInit(RectF viewRect, AbsRender render, AbsChartModule chartModule) {
    super.onInit(viewRect, render, chartModule);
    attribute = render.getAttribute();

    axisLabelPaintLeft.setTypeface(FontConfig.typeFace);
    axisLabelPaintLeft.setTextSize(attribute.gridLabelSize);
    axisLabelPaintLeft.setColor(attribute.gridLabelColor);

    axisLabelPaintRight.setTypeface(FontConfig.typeFace);
    axisLabelPaintRight.setTextSize(attribute.gridLabelSize);
    axisLabelPaintRight.setColor(attribute.gridLabelColor);
    axisLabelPaintRight.setTextAlign(Paint.Align.RIGHT);

    axisPaint.setStyle(Paint.Style.STROKE);
    axisPaint.setStrokeWidth(attribute.axisWidth);
    axisPaint.setColor(attribute.axisColor);

    Utils.measureTextArea(axisLabelPaintLeft, rect);
    textCenter = rect.height() / 2;
  }

  @Override
  public void computePoint(int begin, int end, int current) {

  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    for (int i = 1; i < attribute.axisCount; i++) {
      pointCache[1] = lineBuffer[1] = lineBuffer[3] = viewRect.top + i * regionHeight;
      render.invertMapPoints(pointCache);
      String text = ValueUtils.format(pointCache[1], render.getAdapter().getScale());
      // 绘制横向网格线
      if (attribute.gridLabelLocation == GridLabelLocation.ALL) {
        lineBuffer[5] = lineBuffer[7] = lineBuffer[1];
        lineBuffer[0] = viewRect.left;
        lineBuffer[2] = left - attribute.gridLabelLRMargin;
        lineBuffer[4] = right + attribute.gridLabelLRMargin;
        lineBuffer[6] = viewRect.right;
        canvas.drawLines(lineBuffer, axisPaint);
        canvas.drawText(text, left, lineBuffer[1] + textCenter, axisLabelPaintLeft);
        canvas.drawText(text, right, lineBuffer[5] + textCenter, axisLabelPaintRight);
      } else {
        lineBuffer[0] = viewRect.left;
        lineBuffer[2] = viewRect.right;
        canvas.drawLine(lineBuffer[0], lineBuffer[1], lineBuffer[2], lineBuffer[3], axisPaint);
        if (attribute.gridLabelLocation == GridLabelLocation.LEFT) {
          canvas.drawText(text, left, lineBuffer[1] - rect.top + attribute.gridLabelTBMargin,
              axisLabelPaintLeft);
        } else {
          canvas.drawText(text, right, lineBuffer[1] - rect.top + +attribute.gridLabelTBMargin,
              axisLabelPaintRight);
        }
      }
    }
  }

  @Override
  public void onDrawOver(Canvas canvas) {

  }

  @Override public void onViewChange() {
    regionHeight = viewRect.height() / attribute.axisCount;
    left = viewRect.left + attribute.gridLabelLRMargin;
    right = viewRect.right - attribute.gridLabelLRMargin;
    if (attribute.gridLabelLocation == GridLabelLocation.ALL) {
      left += attribute.gridMarkLineLength;
      right -= attribute.gridMarkLineLength;
    }
  }
}