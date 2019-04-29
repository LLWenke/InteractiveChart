
package com.ll.chart.drawing.timeLine;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import com.ll.chart.compat.attribute.CandleAttribute;
import com.ll.chart.drawing.AbsDrawing;
import com.ll.chart.entry.CandleEntry;
import com.ll.chart.render.CandleRender;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>TimeLineDrawing</p>
 */

public class TimeLineDrawing extends AbsDrawing<CandleRender> {
  private static final String TAG = "TimeLineDrawing";
  private CandleAttribute attribute;//配置文件
  // 边框线画笔
  private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 分时折线画笔(绘制path 尽量不开抗锯齿)
  private Paint timelinePaint = new Paint();
  // 分时阴影画笔(绘制path 尽量不开抗锯齿)
  private Paint timeShaderPaint = new Paint();
  // 分时折线绘制路径
  private Path timelinePath = new Path();
  // 分时阴影绘制路径
  private Path timeShaderPath = new Path();
  // 折线路径位置信息
  private final float[] pathPts = new float[4];
  //间隔
  private float space = 0;
  // 计算 1 个矩形坐标用的
  private float[] candleRectBuffer = new float[4];

  @Override public void onInit(CandleRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeWidth(attribute.borderWidth);
    borderPaint.setColor(attribute.borderColor);

    timelinePaint.setStrokeWidth(attribute.timeLineWidth);
    timelinePaint.setColor(attribute.timeLineColor);
    timelinePaint.setStyle(Paint.Style.STROKE);

    timeShaderPaint.setShader(
        new LinearGradient(0, viewRect.top, 0, viewRect.bottom,
            new int[] { attribute.timeLineShaderColorBegin, attribute.timeLineShaderColorEnd },
            null, Shader.TileMode.REPEAT));

    space = (attribute.candleSpace / attribute.candleWidth) / 2;
  }

  @Override
  public void computePoint(int begin, int end, int current) {
    CandleEntry entry = render.getAdapter().getItem(current);
    pathPts[0] = current;
    pathPts[1] = entry.getClose().value;
    pathPts[2] = current + 0.5f;
    render.mapPoints(pathPts);
    if (current == begin) {//开始点
      timelinePath.moveTo(pathPts[0], pathPts[1]);
      timeShaderPath.moveTo(pathPts[0], viewRect.bottom);
      timeShaderPath.lineTo(pathPts[0], pathPts[1]);
    } else if (current == end - 1) {//结束点
      float endX = pathPts[2] + (pathPts[2] - pathPts[0]);
      timelinePath.lineTo(endX, pathPts[1]);
      timeShaderPath.lineTo(endX, pathPts[1]);
      timeShaderPath.lineTo(endX, viewRect.bottom);
    } else {
      timelinePath.lineTo(pathPts[2], pathPts[1]);
      timeShaderPath.lineTo(pathPts[2], pathPts[1]);
    }
    candleRectBuffer[0] = current + space;
    candleRectBuffer[2] = current + 1 - space;
    render.mapPoints(candleRectBuffer);
    // 计算高亮坐标
    if (render.isHighlight()) {
      final float[] highlightPoint = render.getHighlightPoint();
      if (candleRectBuffer[0] <= highlightPoint[0] && highlightPoint[0] <= candleRectBuffer[2]) {
        highlightPoint[0] = pathPts[2];
        render.getAdapter().setHighlightIndex(current);
      }
    }
  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    canvas.save();
    canvas.clipRect(viewRect);
    canvas.drawPath(timelinePath, timelinePaint);
    canvas.drawPath(timeShaderPath, timeShaderPaint);
    timelinePath.reset();
    timeShaderPath.reset();
    canvas.restore();
  }

  @Override
  public void onDrawOver(Canvas canvas) {

  }

  @Override public void onViewChange() {

  }
}
