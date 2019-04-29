
package com.ll.chart.drawing.depth;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import com.ll.chart.adapter.DepthAdapter;
import com.ll.chart.compat.attribute.DepthAttribute;
import com.ll.chart.drawing.AbsDrawing;
import com.ll.chart.entry.DepthEntry;
import com.ll.chart.render.DepthRender;
import com.ll.chart.module.base.AbsChartModule;

/**
 * <p>DepthDrawing</p>
 */

public class DepthDrawing extends AbsDrawing<DepthRender> {
  private static final String TAG = "DepthDrawing";
  private DepthAttribute attribute;//配置文件
  // 边框线画笔
  private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
  // 买单折线画笔(绘制path 尽量不开抗锯齿)
  private Paint bidPolylinePaint = new Paint();
  // 卖单折线画笔(绘制path 尽量不开抗锯齿)
  private Paint askPolylinePaint = new Paint();
  // 买单阴影画笔(绘制path 尽量不开抗锯齿)
  private Paint bidShaderPaint = new Paint();
  // 卖单阴影画笔(绘制path 尽量不开抗锯齿)
  private Paint askShaderPaint = new Paint();
  // 买单绘制路径
  private Path bidPath = new Path();
  // 卖单绘制路径
  private Path askPath = new Path();
  // 绘制路径
  private Path path;
  // 路径位置信息
  private final float[] pathPts = new float[2];
  // 上一个entry的类型
  private int previousType = -1;
  //用于判断是否计算高亮元素
  private float left, right;
  //用于修正折线偏移量
  private float offset;

  @Override public void onInit(DepthRender render, AbsChartModule chartModule) {
    super.onInit(render, chartModule);
    attribute = render.getAttribute();

    borderPaint.setStyle(Paint.Style.STROKE);
    borderPaint.setStrokeWidth(attribute.borderWidth);
    borderPaint.setColor(attribute.borderColor);

    bidPolylinePaint.setStrokeWidth(attribute.polylineWidth);
    bidPolylinePaint.setColor(attribute.bidLineColor);
    bidPolylinePaint.setStyle(Paint.Style.STROKE);

    askPolylinePaint.setStrokeWidth(attribute.polylineWidth);
    askPolylinePaint.setColor(attribute.askLineColor);
    askPolylinePaint.setStyle(Paint.Style.STROKE);

    bidShaderPaint.setColor(attribute.bidShaderColor);
    bidShaderPaint.setStyle(Paint.Style.FILL);

    askShaderPaint.setColor(attribute.askShaderColor);
    askShaderPaint.setStyle(Paint.Style.FILL);

    offset = chartModule.getxOffset();
  }

  @Override
  public void computePoint(int begin, int end, int current) {
    float x0, x1;
    DepthEntry entry = render.getAdapter().getItem(current);
    pathPts[0] = entry.getPrice().value;
    pathPts[1] = entry.getTotalAmount().value;
    //计算数据点
    if (previousType != entry.getType()) {
      previousType = entry.getType();
      path = previousType == DepthAdapter.BID ? bidPath : askPath;
      offset = -offset;
      render.mapPoints(pathPts, offset, 0);
      path.moveTo(pathPts[0], viewRect.bottom);
    } else {
      render.mapPoints(pathPts, offset, 0);
    }
    path.lineTo(pathPts[0], pathPts[1]);
    //高亮点查找范围
    x0 = pathPts[0];
    //计算补位点
    if (current + 1 < end) {
      //获取补位点信息
      DepthEntry fillEntry = render.getAdapter().getItem(current + 1);
      if (previousType == fillEntry.getType()) {
        pathPts[0] = fillEntry.getPrice().value;
        pathPts[1] = entry.getTotalAmount().value;
        render.mapPoints(pathPts, offset, 0);
        path.lineTo(pathPts[0], pathPts[1]);
        x1 = pathPts[0];
      } else {
        path.lineTo(left, pathPts[1]);
        path.lineTo(left, viewRect.bottom);
        x1 = left;
      }
    } else {
      path.lineTo(right, pathPts[1]);
      path.lineTo(right, viewRect.bottom);
      x1 = right;
    }
    // 计算高亮坐标
    if (render.isHighlight() && previousType == DepthAdapter.BID ?
        (render.getHighlightPoint()[0] >= x1 && render.getHighlightPoint()[0] <= x0)
        : (render.getHighlightPoint()[0] >= x0 && render.getHighlightPoint()[0] <= x1)) {
      render.getHighlightPoint()[1] = pathPts[1];
      render.getAdapter().setHighlightIndex(current);
    }
  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
    canvas.save();
    canvas.clipRect(viewRect);
    canvas.drawPath(bidPath, bidShaderPaint);
    canvas.drawPath(askPath, askShaderPaint);
    canvas.drawPath(bidPath, bidPolylinePaint);
    canvas.drawPath(askPath, askPolylinePaint);
    bidPath.reset();
    askPath.reset();
    canvas.restore();
  }

  @Override
  public void onDrawOver(Canvas canvas) {
    // 绘制外层边框线
    if (attribute.borderWidth > 0) {
      canvas.drawRect(viewRect.left - render.getBorderCorrection(),
          viewRect.top - render.getBorderCorrection(),
          viewRect.right + render.getBorderCorrection(),
          viewRect.bottom + render.getBorderCorrection(),
          borderPaint);
    }
  }

  @Override public void onViewChange() {
    left = viewRect.left - attribute.polylineWidth;
    right = viewRect.right + attribute.polylineWidth;
  }
}
