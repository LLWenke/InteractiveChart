
package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.module.base.AbsChartModule;
import com.ll.chart.render.CandleRender;

/**
 * <p>BOLLDrawing</p>
 */

public class BOLLDrawing extends AbsDrawing<CandleRender> {
  private CandleAttribute attribute;//配置文件

  private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //边框画笔
  private Paint centerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //中心线画笔

  private Paint r1Paint = new Paint();
  private Paint r2Paint = new Paint();
  private Paint r3Paint = new Paint();

  // r1绘制路径
  private Path r1Path = new Path();
  // r2绘制路径
  private Path r2Path = new Path();
  // r3绘制路径
  private Path r3Path = new Path();
  // 折线路径位置信息
  private final float[] pathPts = new float[6];

  private float[] gridBuffer = new float[2];

  @Override public void onInit(CandleRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    centerLinePaint.setColor(attribute.centerLineColor);
    centerLinePaint.setStrokeWidth(attribute.lineWidth);
    centerLinePaint.setStyle(Paint.Style.FILL);

    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeWidth(attribute.borderWidth);
    borderPaint.setColor(attribute.borderColor);

    r1Paint.setColor(attribute.bollMidLineColor);
    r1Paint.setStrokeWidth(attribute.lineWidth);
    r1Paint.setStyle(Paint.Style.STROKE);

    r2Paint.setColor(attribute.bollUpperLineColor);
    r2Paint.setStrokeWidth(attribute.lineWidth);
    r2Paint.setStyle(Paint.Style.STROKE);

    r3Paint.setColor(attribute.bollLowerLineColor);
    r3Paint.setStrokeWidth(attribute.lineWidth);
    r3Paint.setStyle(Paint.Style.STROKE);
  }

  @Override
  public void computePoint(int begin, int end, int current) {
    final CandleEntry entry = render.getAdapter().getItem(current);
    pathPts[0] = pathPts[2] = pathPts[4] = current + 0.5f;
    pathPts[1] = entry.getMb().value;
    pathPts[3] = entry.getUp().value;
    pathPts[5] = entry.getDn().value;
    render.mapPoints(pathPts);
    if (begin == current) {
      r1Path.moveTo(pathPts[0], pathPts[1]);
      r2Path.moveTo(pathPts[2], pathPts[3]);
      r3Path.moveTo(pathPts[4], pathPts[5]);
    } else {
      r1Path.lineTo(pathPts[0], pathPts[1]);
      r2Path.lineTo(pathPts[2], pathPts[3]);
      r3Path.lineTo(pathPts[4], pathPts[5]);
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

    canvas.drawPath(r1Path, r1Paint);
    canvas.drawPath(r2Path, r2Paint);
    canvas.drawPath(r3Path, r3Paint);
    r1Path.reset();
    r2Path.reset();
    r3Path.reset();

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
