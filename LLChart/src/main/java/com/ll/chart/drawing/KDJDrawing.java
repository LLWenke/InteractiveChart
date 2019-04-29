
package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.render.CandleRender;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>KDJDrawing</p>
 */

public class KDJDrawing extends AbsDrawing<CandleRender> {
  private CandleAttribute attribute;//配置文件

  private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //边框画笔
  private Paint centerLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG); //中心线画笔

  private Paint kPaint = new Paint();
  private Paint dPaint = new Paint();
  private Paint jPaint = new Paint();

  // k绘制路径
  private Path kPath = new Path();
  // d绘制路径
  private Path dPath = new Path();
  // j绘制路径
  private Path jPath = new Path();
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

    kPaint.setStyle(Paint.Style.STROKE);
    kPaint.setStrokeWidth(attribute.lineWidth);
    kPaint.setColor(attribute.kdjKLineColor);

    dPaint.setStyle(Paint.Style.STROKE);
    dPaint.setStrokeWidth(attribute.lineWidth);
    dPaint.setColor(attribute.kdjDLineColor);

    jPaint.setStyle(Paint.Style.STROKE);
    jPaint.setStrokeWidth(attribute.lineWidth);
    jPaint.setColor(attribute.kdjJLineColor);
  }

  @Override
  public void computePoint(int begin, int end, int current) {
    final CandleEntry entry = render.getAdapter().getItem(current);
    pathPts[0] = pathPts[2] = pathPts[4] = current + 0.5f;
    pathPts[1] = entry.getK().value;
    pathPts[3] = entry.getD().value;
    pathPts[5] = entry.getJ().value;
    render.mapPoints(pathPts);
    if (begin == current) {
      kPath.moveTo(pathPts[0], pathPts[1]);
      dPath.moveTo(pathPts[2], pathPts[3]);
      jPath.moveTo(pathPts[4], pathPts[5]);
    } else {
      kPath.lineTo(pathPts[0], pathPts[1]);
      dPath.lineTo(pathPts[2], pathPts[3]);
      jPath.lineTo(pathPts[4], pathPts[5]);
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

    canvas.drawPath(kPath, kPaint);
    canvas.drawPath(dPath, dPaint);
    canvas.drawPath(jPath, jPaint);
    kPath.reset();
    dPath.reset();
    jPath.reset();

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
