
package com.ll.chart.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.enumeration.ModuleType;
import com.ll.chart.render.CandleRender;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>MADrawing</p>
 */

public class MADrawing extends AbsDrawing<CandleRender> {
  private static final String TAG = "MADrawing";
  private CandleAttribute attribute;//配置文件

  private Paint ma5Paint = new Paint();
  private Paint ma10Paint = new Paint();
  private Paint ma20Paint = new Paint();
  // ma5绘制路径
  private Path ma5Path = new Path();
  // ma10绘制路径
  private Path ma10Path = new Path();
  // ma20绘制路径
  private Path ma20Path = new Path();
  // 折线路径位置信息
  private final float[] pathPts = new float[6];

  @Override public void onInit(CandleRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    ma5Paint.setStyle(Paint.Style.STROKE);
    ma5Paint.setStrokeWidth(attribute.lineWidth);
    ma5Paint.setColor(attribute.ma5Color);

    ma10Paint.setStyle(Paint.Style.STROKE);
    ma10Paint.setStrokeWidth(attribute.lineWidth);
    ma10Paint.setColor(attribute.ma10Color);

    ma20Paint.setStyle(Paint.Style.STROKE);
    ma20Paint.setStrokeWidth(attribute.lineWidth);
    ma20Paint.setColor(attribute.ma20Color);
  }

  @Override
  public void computePoint(int begin, int end, int current) {
    final CandleEntry entry = render.getAdapter().getItem(current);
    pathPts[0] = pathPts[2] = pathPts[4] = current + 0.5f;
    switch (absChartModule.getModuleType()) {
      case CANDLE:
        pathPts[1] = entry.getMa5().value;
        pathPts[3] = entry.getMa10().value;
        pathPts[5] = entry.getMa20().value;
        render.mapPoints(pathPts);
        if (begin == current) {
          ma5Path.moveTo(pathPts[0], pathPts[1]);
          ma10Path.moveTo(pathPts[2], pathPts[3]);
          ma20Path.moveTo(pathPts[4], pathPts[5]);
        } else {
          ma5Path.lineTo(pathPts[0], pathPts[1]);
          ma10Path.lineTo(pathPts[2], pathPts[3]);
          ma20Path.lineTo(pathPts[4], pathPts[5]);
        }
        break;
      case VOLUME:
        pathPts[1] = entry.getVolumeMa5().value;
        pathPts[3] = entry.getVolumeMa10().value;
        render.mapPoints(pathPts);
        if (begin == current) {
          ma5Path.moveTo(pathPts[0], pathPts[1]);
          ma10Path.moveTo(pathPts[2], pathPts[3]);
        } else {
          ma5Path.lineTo(pathPts[0], pathPts[1]);
          ma10Path.lineTo(pathPts[2], pathPts[3]);
        }
        break;
    }
  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    canvas.save();
    canvas.clipRect(viewRect);
    canvas.drawPath(ma5Path, ma5Paint);
    canvas.drawPath(ma10Path, ma10Paint);
    if (absChartModule.getModuleType() == ModuleType.CANDLE) {
      canvas.drawPath(ma20Path, ma20Paint);
    }
    ma5Path.reset();
    ma10Path.reset();
    ma20Path.reset();
    canvas.restore();
  }

  @Override
  public void onDrawOver(Canvas canvas) {
  }

  @Override public void onViewChange() {

  }
}
