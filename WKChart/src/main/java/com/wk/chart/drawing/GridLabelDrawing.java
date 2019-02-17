
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
 * <p>GridLabelDrawing</p>
 */

public class GridLabelDrawing extends AbsDrawing<AbsRender> {
  private BaseAttribute attribute;//配置文件

  private TextPaint gridLabelPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);// grid 轴标签的画笔
  private TextPaint gridLabelDiffPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);// grid 轴标签的diff画笔
  private Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // axis 轴的画笔
  private Paint axisDiffPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // axis 轴的diff画笔
  private Rect rect = new Rect();//用于测量文字的实际占用区域
  private float[] lineBuffer;// 计算横线线坐标用的
  // 用于 gridLabelDiffPaint 计算文字位置
  private float labelX; //标签X轴的坐标

  @Override public void onInit(RectF viewRect, AbsRender render, AbsChartModule chartModule) {
    super.onInit(viewRect, render, chartModule);
    attribute = render.getAttribute();

    gridLabelPaint.setTextSize(attribute.gridLabelSize);
    gridLabelPaint.setColor(attribute.gridLabelColor);
    gridLabelPaint.setTypeface(FontConfig.typeFace);

    gridLabelDiffPaint.setTextSize(attribute.gridLabelSize);
    gridLabelDiffPaint.setColor(attribute.gridLabelDiffColor);
    gridLabelDiffPaint.setTypeface(FontConfig.typeFace);

    axisPaint.setStyle(Paint.Style.STROKE);
    axisPaint.setStrokeWidth(attribute.axisWidth);
    axisPaint.setColor(attribute.axisColor);

    axisDiffPaint.setStyle(Paint.Style.STROKE);
    axisDiffPaint.setStrokeWidth(attribute.axisWidth);
    axisDiffPaint.setColor(attribute.axisDiffColor);

    if (attribute.gridLabelLocation != GridLabelLocation.LEFT) {
      gridLabelPaint.setTextAlign(Paint.Align.RIGHT);
      gridLabelDiffPaint.setTextAlign(Paint.Align.RIGHT);
    }
    Utils.measureTextArea(gridLabelPaint, rect);
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
        paint = axisDiffPaint;
        textPaint = gridLabelDiffPaint;
        canvas.drawText(ValueUtils.formatBig(absChartModule.getMaxY().value), labelX,
            viewRect.top - rect.top + attribute.gridLabelTBMargin, textPaint);
        break;
      case CANDLE:
        paint = axisPaint;
        textPaint = gridLabelPaint;
        canvas.drawText(
            absChartModule.getMaxY().text,
            labelX,
            viewRect.top - rect.top + attribute.gridLabelTBMargin,
            textPaint);
        break;
      default:
        paint = axisPaint;
        textPaint = gridLabelPaint;
        canvas.drawText(
            ValueUtils.format(extremum[3], render.getAdapter().getScale()),
            labelX,
            viewRect.top - rect.top + attribute.gridLabelTBMargin,
            textPaint);

        canvas.drawText(
            ValueUtils.format(extremum[1], render.getAdapter().getScale()),
            labelX,
            viewRect.bottom - rect.bottom - attribute.gridLabelTBMargin,
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
    labelX = attribute.gridLabelLocation == GridLabelLocation.LEFT ?
        viewRect.left + attribute.gridLabelLRMargin : viewRect.right - attribute.gridLabelLRMargin;
  }
}
