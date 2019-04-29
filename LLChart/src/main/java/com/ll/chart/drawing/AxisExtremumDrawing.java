
package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import com.ll.chart.compat.FontConfig;
import com.ll.chart.compat.Utils;
import com.ll.chart.compat.ValueUtils;
import com.ll.chart.compat.attribute.BaseAttribute;
import com.ll.chart.enumeration.AxisLabelLocation;
import com.ll.chart.render.AbsRender;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>AxisExtremumDrawing</p>
 * Axis极值组件
 */

public class AxisExtremumDrawing extends AbsDrawing<AbsRender> {
  private BaseAttribute attribute;//配置文件

  private TextPaint labelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);// 标签画笔
  private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 标线画笔
  private Rect rect = new Rect();//用于测量文字的实际占用区域
  private float[] lineBuffer;// 计算横线线坐标用的
  private float labelX; //标签X轴的坐标

  @Override public void onInit(AbsRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    labelPaint.setTextSize(attribute.labelSize);
    labelPaint.setColor(attribute.labelColor);
    labelPaint.setTypeface(FontConfig.typeFace);

    linePaint.setStyle(Paint.Style.STROKE);
    linePaint.setStrokeWidth(attribute.lineWidth);
    linePaint.setColor(attribute.lineColor);

    if (attribute.axisLabelLocation != AxisLabelLocation.LEFT) {
      labelPaint.setTextAlign(Paint.Align.RIGHT);
    }
    Utils.measureTextArea(labelPaint, rect);
  }

  @Override
  public void computePoint(int begin, int end, int current) {

  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    Paint paint;
    TextPaint textPaint;
    switch (absChartModule.getModuleType()) {
      case VOLUME:
        paint = linePaint;
        textPaint = labelPaint;
        canvas.drawText(ValueUtils.formatBig(absChartModule.getMaxY().value), labelX,
            viewRect.top - rect.top + attribute.axisLabelTBMargin, textPaint);
        break;
      case CANDLE:
        paint = linePaint;
        textPaint = labelPaint;
        canvas.drawText(
            absChartModule.getMaxY().text,
            labelX,
            viewRect.top - rect.top + attribute.axisLabelTBMargin,
            textPaint);
        break;
      default:
        paint = linePaint;
        textPaint = labelPaint;
        canvas.drawText(
            ValueUtils.format(extremum[3], render.getAdapter().getScale()),
            labelX,
            viewRect.top - rect.top + attribute.axisLabelTBMargin,
            textPaint);

        canvas.drawText(
            ValueUtils.format(extremum[1], render.getAdapter().getScale()),
            labelX,
            viewRect.bottom - rect.bottom - attribute.axisLabelTBMargin,
            textPaint);
        break;
    }
    canvas.drawLines(lineBuffer, paint);
  }

  @Override
  public void onDrawOver(Canvas canvas) {

  }

  @Override public void onViewChange() {
    //设置横线坐标（共两条，top和bottom）
    switch (absChartModule.getModuleType()) {
      case VOLUME:
      case CANDLE:
        lineBuffer = new float[4];
        lineBuffer[0] = viewRect.left;
        lineBuffer[1] = viewRect.top;
        lineBuffer[2] = viewRect.right;
        lineBuffer[3] = viewRect.top;
        break;
      default:
        lineBuffer = new float[8];
        lineBuffer[0] = viewRect.left;
        lineBuffer[1] = viewRect.top;
        lineBuffer[2] = viewRect.right;
        lineBuffer[3] = viewRect.top;
        lineBuffer[4] = viewRect.left;
        lineBuffer[5] = viewRect.bottom;
        lineBuffer[6] = viewRect.right;
        lineBuffer[7] = viewRect.bottom;
        break;
    }
    labelX = attribute.axisLabelLocation == AxisLabelLocation.LEFT ?
        viewRect.left + attribute.axisLabelLRMargin : viewRect.right - attribute.axisLabelLRMargin;
  }
}
