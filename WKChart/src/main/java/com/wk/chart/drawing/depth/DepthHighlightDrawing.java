
package com.wk.chart.drawing.depth;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import com.wk.chart.adapter.DepthAdapter;
import com.wk.chart.compat.attribute.DepthAttribute;
import com.wk.chart.drawing.AbsDrawing;
import com.wk.chart.entry.DepthEntry;
import com.wk.chart.render.DepthRender;
import com.wk.chart.stock.base.AbsChartModule;

/**
 * <p>DepthHighlightDrawing</p>
 */

public class DepthHighlightDrawing extends AbsDrawing<DepthRender> {
  private static final String TAG = "DepthHighlightDrawing";
  private DepthAttribute attribute;//配置文件

  private Paint bidHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 买单高亮线条画笔
  private Paint askHighlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 卖单高亮线条画笔
  private Paint bidCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//买单圆点画笔
  private Paint askCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//卖单圆点画笔

  private float[] highlightPoint = new float[2];//高亮线条x，y

  @Override public void onInit(RectF viewRect, DepthRender render, AbsChartModule chartModule) {
    super.onInit(viewRect, render, chartModule);
    attribute = render.getAttribute();

    bidHighlightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    bidHighlightPaint.setColor(attribute.bidHighlightColor);
    bidHighlightPaint.setStrokeWidth(attribute.polylineWidth);

    askHighlightPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    askHighlightPaint.setColor(attribute.askHighlightColor);
    askHighlightPaint.setStrokeWidth(attribute.polylineWidth);

    bidCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    bidCirclePaint.setColor(attribute.bidLineColor);
    bidCirclePaint.setStrokeWidth(attribute.circleSize);

    askCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
    askCirclePaint.setColor(attribute.askLineColor);
    askCirclePaint.setStrokeWidth(attribute.circleSize);
  }

  @Override
  public void computePoint(int begin, int end, int current) {
  }

  @Override
  public void onComputeOver(Canvas canvas, int begin, int end, float[] extremum) {
  }

  @Override
  public void onDrawOver(Canvas canvas) {
    if (!render.isHighlight()) {
      return;
    }
    // 绘制高亮
    Paint circlePant;//圆点画笔1
    Paint highlightPaint;//高亮线画笔
    DepthEntry entry = render.getAdapter().getItem(render.getAdapter().getHighlightIndex());
    //获取当前焦点区域内的chartModule
    AbsChartModule chartModule = render.getChartModuleInFocusArea();
    if (null == chartModule) {
      return;
    }
    //区分区域
    if (entry.getType() == DepthAdapter.BID) {
      circlePant = bidCirclePaint;
      highlightPaint = bidHighlightPaint;
    } else {
      circlePant = askCirclePaint;
      highlightPaint = askHighlightPaint;
    }

    switch (chartModule.getModuleType()) {
      case DEPTH://深度图指标
        highlightPoint[0] = render.getHighlightPoint()[0];
        highlightPoint[1] = render.getHighlightPoint()[1];
        break;
      default:
        highlightPoint[0] = render.getHighlightPoint()[0];
        highlightPoint[1] = render.getHighlightPoint()[1];
        break;
    }
    canvas.drawCircle(highlightPoint[0], highlightPoint[1], attribute.circleSize / 2,
        circlePant);
    canvas.drawLines(render.getMeasureUtils().buildViewLRCoordinates(highlightPoint[0],
        highlightPoint[0], chartModule.getRect().top, chartModule.getRect().bottom,
        chartModule.getRect()), highlightPaint);
  }

  @Override public void onViewChange() {

  }
}
